package com.freewayemi.merchant.dto.sales;

import lombok.Data;

import java.util.List;

@Data
public class HomeAnalyticsData {

    private TargetVolume targetVolume;
    private long transactingMerchants;
    private long transactingMerchantsToday;
    private List<TransactionModel> recentTxns;
}
