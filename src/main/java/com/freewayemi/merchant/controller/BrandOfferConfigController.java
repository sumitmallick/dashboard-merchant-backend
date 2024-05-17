package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.BrandOfferConfigBO;
import com.freewayemi.merchant.dto.response.BrandOfferConfigResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandOfferConfigController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandOfferConfigController.class);
    private final BrandOfferConfigBO brandOfferConfigBO;

    @Autowired
    public BrandOfferConfigController(BrandOfferConfigBO brandOfferConfigBO) {
        this.brandOfferConfigBO = brandOfferConfigBO;
    }

    @GetMapping("/api/v1/brand/offers")
    public List<BrandOfferConfigResp> getBrandOffer(
            @RequestParam("brandId") String[] brandIds,
            @RequestParam("offerType") String offerType) {
        LOGGER.info("Request received to get brand offers for offerType: {}", offerType );
        return brandOfferConfigBO.getBrandOffer(brandIds, offerType);
    }
}
