package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TransactionDataByMerchant {

    private List<WeeklyTransaction> weeklyTransactions;
    private Long monthlySuccess;
    private String currMonthGrowth;
    private Long todaySuccess;
    private Long todayAttempted;
    private Long totalSuccessTransactions;
    private Boolean currMonthGrownSign;

}