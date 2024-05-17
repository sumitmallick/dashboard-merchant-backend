package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeAnalytics {
    private String name;
    private long targetVolume;
    private String volume;
    private String volumeToday;
    private String txns;
    private String txnsToday;
    private long merchants;
    private long merchantsToday;
    private long transactingMerchants;
    private long transactingMerchantsToday;
    private List<MerchantInfo> recentMerchants;
    private List<TransactionModel> recentTxns;
    private long activities;
    private int latestAppVer;
    private int nonActivatedMerchants;
}
