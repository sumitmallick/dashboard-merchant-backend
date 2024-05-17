package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreUserRewardInfo {
    private final String storeUserId;
    private final Float currTransactionAmount;
    private final Float monthlyTransactionAmount;
    private final Long totalTransactionCount;


    @JsonCreator
    public StoreUserRewardInfo(@JsonProperty("storeUserId") String storeUserId,
                               @JsonProperty("currTransactionAmount") Float currTransactionAmount,
                               @JsonProperty("monthlyTransactionAmount") Float monthlyTransactionAmount,
                               @JsonProperty("totalTransactionCount") Long totalTransactionCount
    ) {
        this.storeUserId = storeUserId;
        this.currTransactionAmount = currTransactionAmount;
        this.monthlyTransactionAmount = monthlyTransactionAmount;
        this.totalTransactionCount = totalTransactionCount;
    }
}
