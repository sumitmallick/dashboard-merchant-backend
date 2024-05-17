package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantSettlement {
    private int transactionAmount;
    private double chargeDeduction;
    private double settlementAmount;
}
