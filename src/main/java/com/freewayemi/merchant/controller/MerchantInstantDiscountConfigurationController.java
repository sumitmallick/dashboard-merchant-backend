package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.BrandBO;
import com.freewayemi.merchant.bo.MerchantInstantDiscountConfigurationBO;
import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.MerchantInstantDiscountConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantInstantDiscountConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantInstantDiscountConfigurationController.class);

    private final MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO;
    private final BrandBO brandBO;

    public MerchantInstantDiscountConfigurationController(MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO, BrandBO brandBO) {
        this.merchantInstantDiscountConfigurationBO = merchantInstantDiscountConfigurationBO;
        this.brandBO = brandBO;
    }

    @GetMapping("/api/v1/merchant/{mid}/brand/{bid}/instant/discount/config")
    public MerchantInstantDiscountConfigResp getMerchantInstantDiscountConfiguration(
            @PathVariable("mid") String merchantId, @PathVariable("bid") String brandId){
        LOGGER.info("MerchantInstantDiscountConfigurationResponse : getMerchantInstantDiscountConfiguration : Starts : " +
                "merchantId: {}, brandId: {}", merchantId, brandId);

        Brand brand = brandBO.findById(brandId);
        if(brand == null){
            LOGGER.error("MerchantInstantDiscountConfigurationResponse : getMerchantInstantDiscountConfiguration : " +
                    "merchantId: {}, brandId: {}, Brand is null.", merchantId, brandId);
            return null;
        }
        return merchantInstantDiscountConfigurationBO.getMerchantInstantDiscountConfigurationResp(merchantId, brandId, brand.getBrandFeeRateInstantDiscount());
    }
}