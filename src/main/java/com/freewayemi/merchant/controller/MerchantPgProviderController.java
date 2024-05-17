package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantPgProviderBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.dto.response.SupportedBankResponse;
import com.freewayemi.merchant.dto.response.SupportedProvidersResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MerchantPgProviderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPgProviderController.class);

    private final MerchantPgProviderBO merchantPgProviderBO;
    private final AuthCommonService authCommonService;
    private final MerchantUserBO merchantUserBO;

    @Autowired
    public MerchantPgProviderController(MerchantPgProviderBO merchantPgProviderBO, AuthCommonService authCommonService, MerchantUserBO merchantUserBO) {
        this.merchantPgProviderBO = merchantPgProviderBO;
        this.authCommonService = authCommonService;
        this.merchantUserBO = merchantUserBO;
    }

    @GetMapping("/api/v1/merchants/{mId}/supportedBanks")
    public SupportedBankResponse getSupportedBanks(@PathVariable("mId") String merchantId) {
        return merchantPgProviderBO.getSupportedBanks(merchantId);
    }

    @GetMapping("/api/v1/merchants/supportedProviders")
    public SupportedProvidersResponse getSupportedProviders(HttpServletRequest httpServletRequest, @RequestParam(value = "partner", required = false) String partner) {
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getMerchantUser(displayMerchantId,partner);

        LOGGER.info("Received request to get supported providers for merchantId: {}", mu.getId());
        return merchantPgProviderBO.getSupportedProviders(mu);
    }

    @GetMapping("api/v1/merchant/validProviders")
    public SupportedProvidersResponse validSupportProvider(@RequestParam(value = "mid") String merchantId,@RequestParam(value = "partner",required = false) String partner) {
        LOGGER.info("Received request to get valid support providers for merchantId: {}", merchantId);
        if (StringUtils.hasText(merchantId)) {
            MerchantUser merchantUser = merchantUserBO.getMerchantUser(merchantId, partner);
            return merchantPgProviderBO.getSupportedProviders(merchantUser);
        }
        LOGGER.info("MerchantId is not present");
        return null;
    }
}
