package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.MerchantOnboardingRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.ResponseCode;
import com.freewayemi.merchant.pojos.APIResponse;
import com.freewayemi.merchant.service.MerchantOnboardingService;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.commons.bo.AuthUserBO;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class MerchantOnboardingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLeadController.class);

    private final MerchantOnboardingService merchantOnboardingService;
    private final AuthUserBO authUserBO;
    private final AuthCommonService authCommonService;
    private final MerchantUserRepository merchantUserRepository;



    @Autowired
    public MerchantOnboardingController(
            MerchantOnboardingService merchantOnboardingService,
            AuthUserBO authUserBO,
            AuthCommonService authCommonService,
            MerchantUserRepository merchantUserRepository
    ) {
        this.merchantOnboardingService = merchantOnboardingService;
        this.authUserBO = authUserBO;
        this.authCommonService = authCommonService;
        this.merchantUserRepository = merchantUserRepository;
    }

    @PostMapping("/onboarding")
    public ResponseEntity onboardingMerchant(HttpServletRequest httpServletRequest,
                                             @RequestHeader(value = "deviceToken", required = false) String deviceToken,
                                             @RequestParam("onboardingStage") String onboardingStage,
                                             @RequestParam(value = "partner", required = false) String partner,
                                             @RequestBody MerchantOnboardingRequest merchantOnboardingRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).get();
        if (Util.isNotNull(partner) && !partner.equals("")) {
            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
                if (Util.isNull(merchantUserUpdated)) {
                    return ResponseEntity.ok(new APIResponse(202, "ERROR",
                            "merchant doesn't exist with mobile: " + merchantUser.getMobile() + " & partner: " + partner,
                            new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null)
                    ));
                }
                merchantUser = merchantUserUpdated;
            }
        }
        LOGGER.info("Request received to onboard merchant: {}", merchantUser.getId());
        LOGGER.info("Request received to onboard for merchantUser: {}", merchantUser);
        APIResponse response = merchantOnboardingService.onboardingMerchant(merchantUser, merchantOnboardingRequest, onboardingStage, deviceToken);
        if (null == response) {
            return ResponseEntity.ok(BasicResponse.builder()
                    .statusMsg(ResponseCode.MERCHANT_ONBOARDING_FAILED.getStatusMsg())
                    .status(ResponseCode.MERCHANT_ONBOARDING_FAILED.getStatus())
                    .statusCode(ResponseCode.MERCHANT_ONBOARDING_FAILED.getCode())
                    .build());
        }
        return ResponseEntity.ok(response);
    }
}

