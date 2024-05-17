package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.PromotionBO;
import com.freewayemi.merchant.dto.response.PromotionResponse;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PromotionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionBO promotionBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public PromotionController(PromotionBO promotionBO, AuthCommonService authCommonService) {
        this.promotionBO = promotionBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/promotion")
    public PromotionResponse get(HttpServletRequest httpServletRequest) {
        String mid = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Get promotion API called for merchant: {}", mid);
        return promotionBO.getPromotion(mid);
    }
}
