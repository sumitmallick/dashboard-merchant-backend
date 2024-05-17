package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInfo {

    private String bank;
    private String cardType;
    private Float loanAmount;
    private Float irr;
    private Integer offerTenure;
    private Integer advanceEmiTenure;
    private Float dbdRate;
    private Float dbdAmount;
    private Float mbdRate;
    private Float mbdAmount;
    private Float processingFee;
    private Float gstOnProcessingFee;

}
