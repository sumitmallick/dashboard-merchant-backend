package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.StoreUserRewardInfo;
import com.freewayemi.merchant.entity.StoreUserReward;
import com.freewayemi.merchant.enums.RewardState;
import com.freewayemi.merchant.repository.StoreUserRewardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class StoreUserRewardsBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreUserRewardsBO.class);
    private  final StoreUserRewardRepository storeUserRewardRepository;

    @Autowired
    public StoreUserRewardsBO(StoreUserRewardRepository storeUserRewardRepository)
    {
        this.storeUserRewardRepository = storeUserRewardRepository;
    }

    public Double getTotalProcessedRewards(String storeUserId)
    {
        return storeUserRewardRepository.findByStoreUserId(storeUserId).orElse(new ArrayList<>()).stream()
                .filter(o -> o.getRewardState().equals(RewardState.PROCESSED)).mapToDouble(o -> o.getAmount()).sum();
    }

    public long getTotalRewardsCount(String storeUserId) {
        return storeUserRewardRepository.countByStoreUserId(storeUserId);
    }

    public List<StoreUserReward> getRewards(String storeUserId) {
        return storeUserRewardRepository.findByStoreUserId(storeUserId).orElse(new ArrayList<>());
    }

    public void updateStoreUserReward(StoreUserRewardInfo storeUserRewardInfo, String merchantId) {
        if(StringUtils.hasText(storeUserRewardInfo.getStoreUserId())) {
            LOGGER.info("Store User Rewards Info: {}", storeUserRewardInfo);
            if(storeUserRewardInfo.getTotalTransactionCount() == 1) {
                StoreUserReward storeUserReward = new StoreUserReward();
                storeUserReward.setStoreUserId(storeUserRewardInfo.getStoreUserId());
                storeUserReward.setRewardState(RewardState.INITIATED);
                storeUserReward.setAmount(111d);
                storeUserReward.setMerchantId(merchantId);
                storeUserReward.setInitiatedDate(Instant.now());
                storeUserRewardRepository.save(storeUserReward);
            }
            if(storeUserRewardInfo.getMonthlyTransactionAmount() > 100000f &&
                    (storeUserRewardInfo.getMonthlyTransactionAmount() -
                            storeUserRewardInfo.getCurrTransactionAmount() < 100000f)) {
                StoreUserReward storeUserReward = new StoreUserReward();
                storeUserReward.setStoreUserId(storeUserRewardInfo.getStoreUserId());
                storeUserReward.setRewardState(RewardState.INITIATED);
                storeUserReward.setAmount(1000d);
                storeUserReward.setMerchantId(merchantId);
                storeUserReward.setInitiatedDate(Instant.now());
                storeUserRewardRepository.save(storeUserReward);
            }
        }
    }
}
