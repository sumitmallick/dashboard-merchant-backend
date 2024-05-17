package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.MerchantIncentive;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.MerchantIncentivesRepository;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.service.SalesAgentService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
public class MerchantIncentiveBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesAgentService.class);

    private final MerchantIncentivesRepository merchantIncentivesRepository;

    private final MerchantUserRepository merchantUserRepository;

    public MerchantIncentiveBO(MerchantIncentivesRepository merchantIncentivesRepository, MerchantUserRepository merchantUserRepository) {
        this.merchantIncentivesRepository = merchantIncentivesRepository;
        this.merchantUserRepository = merchantUserRepository;
    }

    public Optional<List<MerchantIncentive>> getMerchantIncentivesDetails(String merchantId) {
        return merchantIncentivesRepository.findByMerchantIdAndIdAndLastModifiedDate(merchantId);
    }

    public Boolean checkIfIncentivesExistInMerchant(String merchantId,String incentiveId){
         List<MerchantUser> merchantIncentives= merchantUserRepository.findByIdAndIncentiveIds(merchantId,incentiveId).orElse(new ArrayList<>());
         Boolean mi=Boolean.FALSE;
             if(merchantIncentives.size()>0)
             {
                 mi=Boolean.TRUE;
             }
          return mi;
    }
}
