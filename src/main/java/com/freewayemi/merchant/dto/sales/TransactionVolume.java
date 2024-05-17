package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionVolume {
    private Integer dailyTarget;
    private String dailyAchieved;
    private Integer monthlyTarget;
    private String monthlyAchieved;
    private long onboardedMerchants;
    private long transactingMerchants;
    private long nonTransactingMerchants;

}
