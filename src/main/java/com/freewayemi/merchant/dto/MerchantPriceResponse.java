package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantPriceResponse {

    private String offerId;
    private String tenure;
    private String advanceEmiTenure;
    private String emi;
    private String irr;
    private String cashback;
    private String cashbackType;
    private String additionalCashback;
    private String discount;
    private String additionalDiscount;
    private String processingFee;
    private String gstOnProcessingFee;
    private String downPaymentAmount;
    private String offerType;
    private String totalRepaymentAmount;
}
