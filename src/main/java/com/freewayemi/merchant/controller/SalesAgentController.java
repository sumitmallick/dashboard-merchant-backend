package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantSessionBO;
import com.freewayemi.merchant.bo.SalesAgentBO;
import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.bo.JwtTokenBO;
import com.freewayemi.merchant.commons.dto.TokenRequest;
import com.freewayemi.merchant.commons.dto.TokenResponse;
import com.freewayemi.merchant.dto.request.Account;
import com.freewayemi.merchant.dto.request.AddMerchantRequest;
import com.freewayemi.merchant.dto.request.CreateMerchantRequest;
import com.freewayemi.merchant.dto.sales.*;
import com.freewayemi.merchant.entity.KYCLink;
import com.freewayemi.merchant.entity.SalesAgent;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.SalesAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.freewayemi.merchant.commons.utils.paymentConstants.AUTHUSER_MERCHANT;

@RestController
@RequestMapping("/internal/salesagents/api/v1")
public class SalesAgentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesAgentController.class);
    private final SalesAgentBO salesAgentBO;
    private final AuthUserBO authUserBO;
    private final JwtTokenBO jwtTokenBO;
    private final MerchantSessionBO merchantSessionBO;
    private final AuthCommonService authCommonService;
    private final SalesAgentService salesAgentService;

    @Autowired
    public SalesAgentController(SalesAgentBO salesAgentBO, AuthUserBO authUserBO, JwtTokenBO jwtTokenBO,
                                MerchantSessionBO merchantSessionBO, AuthCommonService authCommonService,
                                SalesAgentService salesAgentService) {
        this.salesAgentBO = salesAgentBO;
        this.authUserBO = authUserBO;
        this.jwtTokenBO = jwtTokenBO;
        this.merchantSessionBO = merchantSessionBO;
        this.authCommonService = authCommonService;
        this.salesAgentService = salesAgentService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createOrLoginSalesAgent(@Valid @RequestBody CreateMerchantRequest request) {
        SalesAgent user = salesAgentBO.getOrCreateUser(request);
        authUserBO.createAuthUser(request.getMobile(), user.getId().toString(), AUTHUSER_MERCHANT, false, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token")
    public ResponseEntity<?> issueToken(@RequestBody TokenRequest request) {
        SalesAgent user = salesAgentBO.getByMobile(request.getMobile());
        authUserBO.validate(user.getId().toString(), request.getOtp());
        TokenResponse resp = new TokenResponse(
                jwtTokenBO.generateToken(user.getId().toString(), "", Collections.singletonList("SALESAGENT")));
        merchantSessionBO.saveSession(user.getId().toString(), user.getMobile(), resp.getToken());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/rewards")
    public ResponseEntity<?> rewards(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        String agent = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        map.put("status", salesAgentBO.checkForRewards(agent));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest httpServletRequest) {
        String agent = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return ResponseEntity.ok(salesAgentBO.getResponse(agent));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Account account, HttpServletRequest httpServletRequest) {
        String agent = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return ResponseEntity.ok(salesAgentBO.updateProfile(agent, account));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(@RequestHeader(value = "AppVersion", required = false) Integer appVersion,
                                          @RequestHeader(value = "DeviceToken") String deviceToken,
                                          @RequestParam String leadOwnerId, @RequestParam String type,
                                          HttpServletRequest httpServletRequest) {
        if (appVersion < 40) {
            Map<String, Boolean> forceUpdate = new HashMap<>();
            forceUpdate.put("forceUpdate", true);
            return ResponseEntity.ok(forceUpdate);
        }
        return ResponseEntity.ok(salesAgentService.getAnalaytics(deviceToken, leadOwnerId, type, httpServletRequest));
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getBrands() {
        return ResponseEntity.ok(salesAgentService.getBrands());
    }

    @GetMapping("/merchants")
    public ResponseEntity<?> getMerchantInfos(@RequestParam(value = "mobile", required = false) String mobile,
                                              @RequestParam(value = "gst", required = false) String gst,
                                              @RequestParam(value = "top", required = false) String top,
                                              @RequestParam(value = "skip", defaultValue = "0") int skip,
                                              @RequestParam(value = "limit", defaultValue = "10") int limit,
                                              HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.getMerchantInfos(mobile, gst, top, skip, limit, httpServletRequest));
    }

    @GetMapping("/v2/merchants")
    public ResponseEntity<?> getMerchantInfosV2(@ModelAttribute MerchantRequest merchantRequest,
                                                HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
                salesAgentService.getMerchantInfosv2(merchantRequest.getText(), merchantRequest.getStatus(),
                        merchantRequest.getSearchType(), merchantRequest.getSkip(), merchantRequest.getLimit(),
                        httpServletRequest));
    }

    @GetMapping("/v3/merchants")
    public ResponseEntity<?> getMerchantInfosV3(@ModelAttribute MerchantRequest merchantRequest,
                                                HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
                salesAgentService.getMerchantInfosv3(merchantRequest.getText(), merchantRequest.getStatus(),
                        merchantRequest.getSearchType(), merchantRequest.getFieldNe(), merchantRequest.getTransacting(),
                        merchantRequest.getSkip(), merchantRequest.getLimit(), httpServletRequest));
    }

    @GetMapping("/merchants/search")
    public ResponseEntity<?> getMerchantInfosV2(@RequestParam(value = "params", required = false) String params,
                                                HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.searchMerchants(params, httpServletRequest));
    }

    @GetMapping("/merchants/{mid}/emiCalculator")
    public ResponseEntity<?> emiCalculator(@PathVariable("mid") String merchantId, @RequestParam int amount,
                                           @RequestParam String cardType) {
        return ResponseEntity.ok(salesAgentService.emiCalculator(amount, cardType));
    }

    @GetMapping("/merchants/{mid}")
    public ResponseEntity<?> getMerchant(@PathVariable("mid") String merchantId) throws ParseException {
        return ResponseEntity.ok(salesAgentService.getMerchant(merchantId, Boolean.FALSE));
    }

    @PostMapping("/onboarding/status/{mid}")
    public ResponseEntity<?> merchantOnboardingStatus(@RequestBody TransactionOpsRequest transactionCountReq, HttpServletRequest httpServletRequest) throws ParseException {
        return ResponseEntity.ok(salesAgentService.merchantOnboardingStatus(transactionCountReq));
    }

    @PostMapping("/merchants")
    public ResponseEntity<?> createMerchant(@RequestHeader(value = "AppVersion", required = false) String appVersion,
                                            @RequestBody AddMerchantRequest addMerchantRequest,
                                            HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.createMerchant(appVersion, addMerchantRequest, httpServletRequest));
    }

    @GetMapping("/merchants/{mid}/pricingProposal")
    public ResponseEntity<?> getPricingProposal(@PathVariable("mid") String merchantId) {
        return ResponseEntity.ok(salesAgentService.getPricingProposal(merchantId));
    }

    @PostMapping("/brandGst")
    public ResponseEntity<?> brandGst(@RequestBody BrandGstRequest brandGstRequest) {
        return ResponseEntity.ok(salesAgentService.brandGst(brandGstRequest));
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestParam(value = "leadOwnerId") String leadOwnerId) {
        return ResponseEntity.ok(salesAgentBO.getSalesUserProfile(leadOwnerId));
    }

    @GetMapping("/digilockerLink/{mid}")
    public ResponseEntity<?> digilockerLink(@PathVariable("mid") String merchantId) {
        return ResponseEntity.ok(salesAgentService.getDigilockerLink(merchantId));
    }

    @GetMapping("/{mid}/incentives")
    public ResponseEntity<?> getMerchantIncentivesDetails(@PathVariable("mid") String merchantId, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.getMerchantIncentivesDetails(merchantId));
    }

    @PutMapping("/notification/{nid}")
    public ResponseEntity<?> updateNotification(@PathVariable("nid") String notificationId, HttpServletRequest httpServletRequest) {

        return ResponseEntity.ok(salesAgentBO.updateNotification(notificationId));
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications(@RequestParam(value = "leadOwnerId") String lid) {
        return ResponseEntity.ok(salesAgentService.getNotification(lid));
    }

    @PostMapping("/saveKycLink")
    public ResponseEntity<?> saveKycLink(@RequestBody KYCLink kycLink) {
        return ResponseEntity.ok(salesAgentService.saveKycLink(kycLink));
    }

    @PostMapping("/merchants/verifyGst")
    public ResponseEntity<?> verifyGstDetails(@RequestBody GstReq gstReq, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.verifyMerchantDetails(gstReq, httpServletRequest));
    }

    @PostMapping("/merchantCount")
    public ResponseEntity<?> getMerchantCount(@RequestBody MerchantCountReq merchantCountReq, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.getMerchantCount(merchantCountReq));
    }

    @PostMapping("/merchants/isOnboarded")
    public ResponseEntity<?> getTransactionCount(@RequestBody TransactionOpsRequest transactionCountReq, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.getOnboardingStatus(transactionCountReq));
    }

    @GetMapping("/min/version")
    public ResponseEntity<?> getMinversion(@RequestHeader(value = "AppVersion") Integer AppVersion, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(salesAgentService.getMinversion(AppVersion));
    }

    @GetMapping("/searchMerchants")
    public ResponseEntity<?> searchMerchants(@RequestParam String leadOwnerId) {
        return ResponseEntity.ok(salesAgentService.getSearchMerchants(leadOwnerId));
    }

    @GetMapping("/visibilities")
    public ResponseEntity<?> visibilities(@RequestParam String merchantId, String status) {
        return ResponseEntity.ok(salesAgentService.getVisibilities(merchantId, status));
    }
}