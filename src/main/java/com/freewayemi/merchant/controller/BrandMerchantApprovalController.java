package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.BrandMerchantApprovalBO;
import com.freewayemi.merchant.commons.dto.BrandMerchantCredentialResponse;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BrandMerchantApprovalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandMerchantApprovalController.class);

    private final BrandMerchantApprovalBO brandMerchantApprovalBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public BrandMerchantApprovalController(BrandMerchantApprovalBO brandMerchantApprovalBO,
                                           AuthCommonService authCommonService) {
        this.brandMerchantApprovalBO = brandMerchantApprovalBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/brandMerchantApproval/checkCredential")
    public BrandMerchantCredentialResponse checkCredential(
            @RequestParam(value = "externalCode", required = false) String externalCode,
            HttpServletRequest httpServletRequest) {
        String brandDisplayId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        ;
        LOGGER.info("Request received for check security credential details by brandDisplayId : {}", brandDisplayId);
        return brandMerchantApprovalBO.checkCredential(brandDisplayId, externalCode);
    }

}
