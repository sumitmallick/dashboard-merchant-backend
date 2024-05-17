package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantOfferConfigBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.dto.offer.DynamicOfferResponse;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DynamicOffersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicOffersController.class);

    private final MerchantOfferConfigBO merchantOfferConfigBO;
    private final MerchantUserBO merchantUserBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public DynamicOffersController(MerchantOfferConfigBO merchantOfferConfigBO,
                                   MerchantUserBO merchantUserBO, AuthCommonService authCommonService) {
        this.merchantOfferConfigBO = merchantOfferConfigBO;
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/dynamicOffers")
    public DynamicOfferResponse getDynamicOfferResponse(HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Getting dynamic offer response for merchant id: {}", merchantId);
        return merchantOfferConfigBO.getDynamicOfferResponse(merchantUserBO.getUserById(merchantId));
    }

    @GetMapping("/api/v1/merchant/{mid}/dynamicOffers")
    public DynamicOfferResponse getDynamicOfferResponseByMerchant(@PathVariable("mid") String mid) {
        LOGGER.info("ADMIN: Getting dynamic offer response for merchant id: {}", mid);
        return merchantOfferConfigBO.getDynamicOfferResponse(merchantUserBO.getUserById(mid));
    }

    @PostMapping("/api/v1/dynamicOffers")
    public void create(@RequestBody DynamicOfferResponse request,
                       @RequestHeader(value = "X-Real-IP", required = false) String ip,
                       HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Creating dynamic offer response for merchant id: {} with Request: {}", merchantId, request);
        merchantUserBO.configurePricing(merchantId, request, ip, "App");
    }

    @PostMapping("/api/v1/merchant/{mid}/dynamicOffers")
    public void createByMerchant(@PathVariable("mid") String mid, @RequestBody DynamicOfferResponse request) {
        LOGGER.info("ADMIN: Creating dynamic offer response for merchant id: {} with request: {}", mid, request);
        merchantUserBO.configurePricing(mid, request, null, null);
    }

}
