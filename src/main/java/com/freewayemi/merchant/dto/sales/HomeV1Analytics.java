package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeV1Analytics {
    private String name;
    private long targetOnboardingPerDay;
    private long achievedOnboardingPerDay;
    private Pending pending;
    private Monthly monthly;
    private Boolean displayRewards;
    private Reward reward;
    private TransactionVolume transactionVolume;
    private Long teamMemberCount;
}
