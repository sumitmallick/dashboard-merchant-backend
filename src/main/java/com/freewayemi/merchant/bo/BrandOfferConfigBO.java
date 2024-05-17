package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.response.BrandOfferConfigResp;
import com.freewayemi.merchant.entity.BrandOfferConfig;
import com.freewayemi.merchant.enums.ConsumerAppBrandOfferType;
import com.freewayemi.merchant.repository.BrandOfferConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BrandOfferConfigBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandOfferConfigBO.class);

    private final BrandOfferConfigRepository brandOfferConfigRepository;

    @Autowired
    public BrandOfferConfigBO(BrandOfferConfigRepository brandOfferConfigRepository) {
        this.brandOfferConfigRepository = brandOfferConfigRepository;
    }

    public List<BrandOfferConfigResp> getBrandOffer(String[] brandIds, String offerType){
        LOGGER.info("Request received to get brand offers with brand ids: {}, offerType :{} ", brandIds , offerType);
        List<BrandOfferConfigResp> brandOfferConfigRespList =  new ArrayList<>();
        Optional<List<BrandOfferConfig>> brandOfferConfigListOptional = null ;
        if(StringUtils.hasText(offerType) && ConsumerAppBrandOfferType.STORE_OFFER.getOfferType().equals(offerType)){
            brandOfferConfigListOptional = brandOfferConfigRepository.findStoreOfferByBrandIdAndIsActive(brandIds, Instant.now(), true);
        }else if(StringUtils.hasText(offerType) && ConsumerAppBrandOfferType.BANK_OFFER.getOfferType().equals(offerType)){
            brandOfferConfigListOptional = brandOfferConfigRepository.findBrandOfferByBrandIdAndIsActive(brandIds, Instant.now(), true);
        }
        if(null != brandOfferConfigListOptional && brandOfferConfigListOptional.isPresent() && brandOfferConfigListOptional.get().size() > 0){
            brandOfferConfigListOptional.get().forEach(brandOfferConfig -> {
                brandOfferConfigRespList.add(new BrandOfferConfigResp(brandOfferConfig));
            });
        }
        return  brandOfferConfigRespList;
    }
}
