package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.enums.RewardState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "store_user_rewards")
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreUserReward extends BaseEntity {
    private String merchantId;
    private String storeUserId;
    private Double amount;
    private RewardState rewardState;
    private Instant initiatedDate;
    private Instant processedDate;
}
