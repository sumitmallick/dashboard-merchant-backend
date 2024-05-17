package com.freewayemi.merchant.dto.sales;

import lombok.Data;

@Data
public class PaymentParams {

    private Boolean processRefundsOnProRetaBasis;
    private Boolean fullRefundWhenPartialRefundRequestAmountIsHigherThanPgAmount;

}