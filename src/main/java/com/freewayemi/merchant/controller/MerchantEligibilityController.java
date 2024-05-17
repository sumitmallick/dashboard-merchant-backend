package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantEligibilitiesBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.dto.CheckEligibilityRequest;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.EligibilityApiType;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import com.freewayemi.merchant.dto.CheckNtbEligibilityRequest;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.request.ValidateOtpRequest;
import com.freewayemi.merchant.dto.response.CheckEligibilityResponse;
import com.freewayemi.merchant.dto.response.CheckEligibilityResponseV2;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.MerchantAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MerchantEligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantEligibilityController.class);

    private final MerchantUserBO merchantUserBO;
    private final MerchantEligibilitiesBO merchantEligibilitiesBO;
    private final MerchantAuthService merchantAuthService;
    private final AuthCommonService authCommonService;

    @Autowired
    public MerchantEligibilityController(MerchantUserBO merchantUserBO, MerchantEligibilitiesBO merchantEligibilitiesBO,
                                         MerchantAuthService merchantAuthService, AuthCommonService authCommonService) {
        this.merchantUserBO = merchantUserBO;
        this.merchantEligibilitiesBO = merchantEligibilitiesBO;
        this.merchantAuthService = merchantAuthService;
        this.authCommonService = authCommonService;
    }

    @PostMapping("/api/v1/eligibility/check")
    public CheckEligibilityResponse checkEligibilityWithOtp(@Valid @RequestBody CheckEligibilityRequest request,
                                                            @RequestParam(value = "partner", required = false)
                                                            String partner, HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to check eligibility with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
        return merchantEligibilitiesBO.getCheckEligibilityWithOtp(mu, request);
    }

    @PostMapping("/api/v1/eligibility/validateOtp")
    public CheckEligibilityResponse validateOtp(@RequestBody ValidateOtpRequest request,
                                                @RequestParam(value = "partner", required = false) String partner,
                                                HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to validate OTP with params: {}", request);

        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);

        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }

        LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
        return merchantEligibilitiesBO.validateOtpAndSendEligibilities(mu, request);
    }

    @PostMapping("/api/v1/eligibility/check/card")
    public CheckEligibilityResponse checkEligibilityWithCardDetails(@RequestBody CheckEligibilityRequest request,
                                                                    @RequestParam(value = "partner", required = false)
                                                                    String partner,
                                                                    HttpServletRequest httpServletRequest) {
        LOGGER.info("Check eligibility with card details request received with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
        return merchantEligibilitiesBO.checkEligibilityWithCardDetails(mu, request,
                EligibilityApiType.MERCHANT_ELIGIBILITY_API_WITH_CARD_DETAILS.name());
    }

    @PostMapping("/api/v1/eligibility/persist")
    public TransactionCode persistEligibilities(@RequestBody CheckEligibilityRequest request) {
        try {
            LOGGER.info("Request received to persist eligibility details with params: {}", request);
            merchantEligibilitiesBO.persistEligibilityDetails(request);
            return TransactionCode.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while saving eligibilities: ", e);
        }
        return TransactionCode.FAILED_20;
    }

    @GetMapping("/distinct/eligibilities/supportedProviders")
    public Map<String, List<String>> getData(final HttpServletRequest request) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        Map<String, List<String>> result = new HashMap<>();
        result.put("supportedProviders", merchantEligibilitiesBO.getDistinctValues());
        return result;
    }

    @PostMapping("/api/v2/checkEligibility")
    public CheckEligibilityResponseV2 checkEligibilityV2(@Valid @RequestBody CheckEligibilityRequest request,
                                                         @RequestParam(value = "partner", required = false)
                                                         String partner, HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to check eligibility with params: {}", request);
        try {
            String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
            MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
            String userMobile = mu.getMobile();

            MerchantUser partnerUser = null;
            List<String> partners = mu.getPartners();
            if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
                if (Util.isNotNull(partners) && partners.contains(partner)) {
                    partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                    mu = partnerUser;
                } else {
                    throw new FreewayException(
                            userMobile + " doesn't have any partner merchant with partner: " + partner);
                }
            }
            LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
            return merchantEligibilitiesBO.checkEligibilityV2(mu, request,
                    EligibilityApiType.MERCHANT_ELIGIBILITY_API.name());
        } catch (Exception e) {
            LOGGER.error("Exception while checking eligibility: ", e);
            throw new FreewayException("Something went wrong!", "merchant", null != request ? request.getEmail() : "");
        }
    }

    @PostMapping("/api/v1/checkNtbEligibility")
    public ResponseEntity<?> checkNtbEligibility(@RequestBody CheckNtbEligibilityRequest checkNtbEligibilityRequest,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to check ntb eligibility with params: {}", checkNtbEligibilityRequest);
        ValidationUtil.validateCheckNtbEligibility(checkNtbEligibilityRequest);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return ResponseEntity.ok(merchantEligibilitiesBO.checkNtbEligibility(checkNtbEligibilityRequest, mu));
    }

    @GetMapping("/api/v1/ntbEligibilityPolling")
    public ResponseEntity<?> ntbEligibilityPolling(@RequestParam("transactionId") String transactionId,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to check ntb poling with params: {}", transactionId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return ResponseEntity.ok(merchantEligibilitiesBO.ntbEligibilityPolling(transactionId, mu));
    }

    @PostMapping("/api/v1/refunds")
    public ResponseEntity<?> ntbRefunds(@RequestParam("transactionId") String transactionId,
                                        @RequestBody RefundTransactionRequest refundTxnReq,
                                        HttpServletRequest httpServletRequest){
        LOGGER.info("Request received to ntb refunds api with params: {}", transactionId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return ResponseEntity.ok(merchantEligibilitiesBO.requestRefund(transactionId, refundTxnReq));
    }

}
