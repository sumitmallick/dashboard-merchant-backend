package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreUserHomeData {
    private final Float targetTransaction;
    private final Float rewardAmount;
    private final Float currentMonthTransactions;
    private final Boolean isAccountDetailsAvailable;
    private final Double totalRewardsEarned;
}
