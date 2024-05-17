package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.EmiOptionsRequest;
import com.freewayemi.merchant.dto.request.ProfileRequest;
import com.freewayemi.merchant.dto.request.VerifyMerchantDetails;
import com.freewayemi.merchant.dto.response.EmiPricingResponse;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.dto.response.VerifyDetailsResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.pojos.APIResponse;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.RuleEngineHelperService;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
public class ProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final MerchantUserBO merchantUserBO;
    private final AuthCommonService authCommonService;
    private final MerchantUserRepository merchantUserRepository;
    private final RuleEngineHelperService ruleEngineHelperService;


    @Autowired
    public ProfileController(MerchantUserBO merchantUserBO, NotificationService notificationService,
                             AuthCommonService authCommonService,
                             MerchantUserRepository merchantUserRepository,
                             RuleEngineHelperService ruleEngineHelperService) {
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
        this.merchantUserRepository = merchantUserRepository;
        this.ruleEngineHelperService = ruleEngineHelperService;
    }

    @PutMapping("/api/v1/profile")
    public APIResponse updateProfile(
            @RequestParam(value = "partner", required = false) String partner,
            @RequestBody ProfileRequest request,
            HttpServletRequest httpServletRequest){
        LOGGER.info("Update merchant profile details received with request: {}", request);
        String user = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser merchantUser = merchantUserRepository.findById(user).get();
        if (Util.isNotNull(partner) && !"partner".equals(partner)) {
            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
                if (Util.isNull(merchantUserUpdated)) {
                    return new APIResponse(231, "ERROR", "There is partner merchant present with mobile: " + merchantUser.getMobile() + " for partner: " + partner, new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
                }
                merchantUser = merchantUserUpdated;
            }
        }
        return merchantUserBO.update(merchantUser, request);
    }

    @PutMapping("/api/v1/verifyDetails")
    public VerifyDetailsResponse verifyDetails(
            @RequestParam(value = "partner", required = false) String partner,
            @RequestBody VerifyMerchantDetails request,
            HttpServletRequest httpServletRequest) {
        LOGGER.info("Verify merchant details received with request: {}", request);
        String user = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser merchantUser = merchantUserRepository.findById(user).get();
        if (Util.isNotNull(partner) && !partner.equals("")) {
            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
                if (Util.isNull(merchantUserUpdated)) {
                    return VerifyDetailsResponse.builder()
                            .status("ERROR")
                            .build();
                }
                merchantUser = merchantUserUpdated;
            }
        }
        return merchantUserBO.verifyDetails(request, merchantUser);
    }


    @PutMapping("/api/v1/uploads")
    public APIResponse upload(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "partner", required = false) String partner,
                              @RequestParam(value = "documentOwnerType", required = false) String documentOwnerType,
                              HttpServletRequest httpServletRequest) throws IOException {
        LOGGER.info("Received request for uploads document  name: {}, type: {}, documentOwnerType: {}", name, type, documentOwnerType);
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).get();
        if (Util.isNotNull(partner) && !partner.equals("")) {
            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
                if (Util.isNull(merchantUserUpdated)) {
                    return new APIResponse(202, "ERROR",
                            "merchant doesn't exist with mobile: " + merchantUser.getMobile() + " & partner: " + partner,
                            new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null)
                    );
                }
                merchantUser = merchantUserUpdated;
            }
        }
        return merchantUserBO.upload(merchantUser.getId().toString(), file, name, type, documentOwnerType);
    }


    @GetMapping("/api/v1/qr/{qid}/merchants")
    public MerchantUserResponse getMerchantByQR(@PathVariable("qid") String qr) {
        return merchantUserBO.getMerchantByQR(qr);
    }

    @GetMapping("/api/v1/pricingProposal")
    public EmiPricingResponse getPrisingProposal() {
        LOGGER.info("Received get request for pricing proposal");
        return merchantUserBO.getEmiPricing();
    }

//    @PostMapping("/api/v1/pricingProposal")
//    public APIResponse setEmiOptions(
//            @RequestParam(value = "partner", required = false) String partner,
//            @RequestBody EmiOptionsRequest request,
//            HttpServletRequest httpServletRequest) {
//        LOGGER.info("Received post request for pricing proposal, request: {}", request);
//        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
//        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).get();
//        if (Util.isNotNull(partner) && !partner.equals("")) {
//            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
//                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
//                if (Util.isNull(merchantUserUpdated)) {
//                    return new APIResponse(202, "ERROR",
//                            "merchant doesn't exist with mobile: " + merchantUser.getMobile() + " & partner: " + partner,
//                            new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null)
//                    );
//                }
//                merchantUser = merchantUserUpdated;
//            }
//        }
//        return merchantUserBO.setEmiOptions(request, merchantUser);
//    }

    @PostMapping("/api/v1/appLaunch")
    public void appLaunch(@RequestHeader Map<String, String> headers) throws IOException{
        ruleEngineHelperService.saveMobileData(headers, null);
    }
}
