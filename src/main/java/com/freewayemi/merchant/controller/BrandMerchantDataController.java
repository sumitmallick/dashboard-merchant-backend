package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.dto.BrandMerchantDataResponse;
import com.freewayemi.merchant.bo.BrandMerchantDataBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandMerchantDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandMerchantDataController.class);
    private final BrandMerchantDataBO brandMerchantDataBO;

    @Autowired
    public BrandMerchantDataController(BrandMerchantDataBO brandMerchantDataBO) {
        this.brandMerchantDataBO = brandMerchantDataBO;
    }

    @GetMapping("/api/v1/brand/merchant/data")
    public BrandMerchantDataResponse getBrandData(@RequestParam("brandId") String brandId,
                                                  @RequestParam("gst") String gst,
                                                  @RequestParam(value = "merchantId", required = false) String merchantId) {
        LOGGER.info("Request received to get brand merchant data for brand id: {}, gst: {}, merchantId : {}", brandId, gst, merchantId);
        return brandMerchantDataBO.searchBrandData(brandId, gst, merchantId);
    }

    @GetMapping("/api/v1/brand/merchantBrandInfo")
    public List<BrandMerchantDataResponse> getMerchantBrandData(@RequestParam ("displayMerchantId") String displayMerchantId,
                                                                @RequestParam(value = "merchantId", required = false) String merchantId){
       LOGGER.info("Received getMerchantBrandData request for displayMerchantId: {} and merchantId: {}",displayMerchantId, merchantId);
       return brandMerchantDataBO.getBrandsForMerchant(displayMerchantId,merchantId);
    }
}
