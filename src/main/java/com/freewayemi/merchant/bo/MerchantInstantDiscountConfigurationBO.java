package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import com.freewayemi.merchant.entity.MerchantInstantDiscountConfiguration;
import com.freewayemi.merchant.repository.MerchantInstantDiscountConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MerchantInstantDiscountConfigurationBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantInstantDiscountConfigurationBO.class);

    private final MerchantInstantDiscountConfigurationRepository merchantInstantDiscountConfigurationRepository;

    @Autowired
    public MerchantInstantDiscountConfigurationBO(
            MerchantInstantDiscountConfigurationRepository merchantInstantDiscountConfigurationRepository) {
        this.merchantInstantDiscountConfigurationRepository = merchantInstantDiscountConfigurationRepository;
    }

    public MerchantInstantDiscountConfiguration get(String id) {
        return merchantInstantDiscountConfigurationRepository.findById(id).orElse(null);
    }

    public MerchantInstantDiscountConfiguration getMerchantInstantDiscountConfiguration(
            String merchantId, String brandId){
        return getMerchantInstantDiscountConfiguration(merchantId, brandId, "active");
    }

    public MerchantInstantDiscountConfiguration getMerchantInstantDiscountConfiguration(
            String merchantId, String brandId, String status){
        Optional<MerchantInstantDiscountConfiguration> merchantInstantDiscountConfigurationOptional =
                merchantInstantDiscountConfigurationRepository.findByMerchantIdAndBrandId(
                        merchantId, brandId, "active");
        return merchantInstantDiscountConfigurationOptional.orElse(null);
    }

    public MerchantInstantDiscountConfigResp getMerchantInstantDiscountConfigurationResp(String merchantId, String brandId, Float brandFeeRateInstantDiscount) {
        MerchantInstantDiscountConfigResp merchantInstantDiscountConfigResp = null;
        MerchantInstantDiscountConfiguration merchantInstantDiscountConfiguration =
                getMerchantInstantDiscountConfiguration(merchantId, brandId);
        if(merchantInstantDiscountConfiguration == null) {
            LOGGER.info("MerchantInstantDiscountConfigurationResponse: merchantId: {}, brandId: {}, " +
                    "Config is null.", merchantId, brandId);
        }
        else {
            merchantInstantDiscountConfigResp =
                    MerchantInstantDiscountConfigResp.builder()
                            .merchantId(merchantInstantDiscountConfiguration.getMerchantId())
                            .brandId(merchantInstantDiscountConfiguration.getBrandId())
                            .offerType(merchantInstantDiscountConfiguration.getOfferType())
                            .additionalMdr(merchantInstantDiscountConfiguration.getAdditionalMdr())
                            .status(merchantInstantDiscountConfiguration.getStatus())
                            .brandFeeRate(brandFeeRateInstantDiscount).build();

            LOGGER.info("MerchantInstantDiscountConfigurationResponse: " +
                    "merchantId: {}, brandId: {}, config: {}", merchantId, brandId, merchantInstantDiscountConfigResp);
        }
        return merchantInstantDiscountConfigResp;
    }
}