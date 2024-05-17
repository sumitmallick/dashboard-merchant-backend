package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.Item;
import com.freewayemi.merchant.repository.IncentivesRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IncentiveBO {

    private final IncentivesRepository incentivesRepository;

    public IncentiveBO(IncentivesRepository incentivesRepository) {
        this.incentivesRepository = incentivesRepository;
    }


    public Optional<Item> getMerchantIncentives(String incentiveId) {
        return incentivesRepository.findById(incentiveId);
    }
}
