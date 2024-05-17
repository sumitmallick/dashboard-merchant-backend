package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.MerchantProperties;
import com.freewayemi.merchant.repository.MerchantProperitiesRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MerchantProperitiesBO {
    private final static Logger logger = LoggerFactory.getLogger(MerchantProperitiesBO.class);

    private final MerchantProperitiesRepository merchantProperitiesRepository;

    @Autowired
    public MerchantProperitiesBO(MerchantProperitiesRepository merchantProperitiesRepository) {
        this.merchantProperitiesRepository = merchantProperitiesRepository;
    }

    public MerchantProperties getMerchantProperties(String merchantId) {
        List<MerchantProperties> merchantPropertiesList =
                merchantProperitiesRepository.findByMerchantId(merchantId).orElse(new ArrayList<>());
        if (!CollectionUtils.isEmpty(merchantPropertiesList)) {
            return merchantPropertiesList.get(0);
        }
        return null;
    }

    public void saveMerchantProperities(MerchantProperties merchantProperties) {
        if (Objects.nonNull(merchantProperties)) {
            MerchantProperties properties =
                    merchantProperitiesRepository.findById(merchantProperties.getMerchantId()).orElse(null);
            if (Objects.nonNull(properties)) {
                if (Objects.nonNull(merchantProperties.getMerchantId())) {
                    properties.setMerchantId(merchantProperties.getMerchantId());
                }
                if (Objects.nonNull(merchantProperties.getMerchandise())) {
                    properties.setMerchandise(merchantProperties.getMerchandise());
                }
                if (Objects.nonNull(merchantProperties.getBrandTags())) {
                    properties.setBrandTags(merchantProperties.getBrandTags());
                }
                if (Objects.nonNull(merchantProperties.getCommercials())) {
                    properties.setCommercials(merchantProperties.getCommercials());
                }
                if (Objects.nonNull(merchantProperties.getProductCategoriesMap())) {
                    properties.setProductCategoriesMap(merchantProperties.getProductCategoriesMap());
                }
                if (Objects.nonNull(merchantProperties.getKyc())) {
                    properties.setKyc(merchantProperties.getKyc());
                }
                merchantProperitiesRepository.save(properties);
            } else {
                merchantProperitiesRepository.save(merchantProperties);
            }
        }
    }
}
