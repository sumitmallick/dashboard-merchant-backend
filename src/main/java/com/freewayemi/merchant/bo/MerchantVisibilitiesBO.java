package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.MerchantVisibilities;
import com.freewayemi.merchant.repository.MerchantVisibilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MerchantVisibilitiesBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantVisibilitiesBO.class);

    private MerchantVisibilityRepository merchantVisibilityRepository;

    @Autowired
    public MerchantVisibilitiesBO(MerchantVisibilityRepository merchantVisibilityRepository){
        this.merchantVisibilityRepository = merchantVisibilityRepository;
    }
    public List<MerchantVisibilities> getMerchantVisibilitiesByMerchantIdAndStatus(String merchantId, String status){
        return merchantVisibilityRepository.findByMerchantIdAndStatus(merchantId, status).orElse(new ArrayList<>());
    }
}
