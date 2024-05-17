package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = PriceResponse.PriceResponseBuilder.class)
@Builder(builderClassName = "PriceResponseBuilder", toBuilder = true)
public class PriceResponse {
    private final String offerId;
    private final Integer tenure;
    private final Float amount;
    private final Float downPayment;
    private final Float discount;
    private final Float bankCharges;
    private final Float emi;
    private final Float settlement;
    private final Float irr;
    private final Float gst;
    private final Float gstPer;
    private final Float netSettlement;
    private final Float discountMerchant;
    private final String effectiveIrr;
    private final Float convFee;
    private final Float gstOnConvFee;
    private final Float cashback;
    private final Instant expectedCashbackDate;
    private final Float pgAmount;
    private final Float processingFee;
    private final Float gstOnProcessingFee;
    private final Integer advanceEmiTenure;
    private final Float discountOnProcessingFee;
    private Float additionalCashback;
    private Float bankProcessingFee;
    private Float bankProcessingIncGst;
    private String cashbackType;
    private Float orderAmount;
    private String downpaymentType;


    /**
     * @Field offlineAdvanceEMITenure
     * This field is a place holder to store tenure value using which payment will calculate the downpayment amount
     * which will be collect by the merchant from customer offline.
     * e.g. amount = 1,00,000
     * tenure = 8
     * offlineAdvanceEMITenure = 2
     * downpayment = 2 * (1,00,000 / (8 + 2)) = 20,000 to be collected by merchant from customer as cash
     * remaining 80,000 will be loan booking amount for 8 months
     * This requirement was raised with Allen Integration LEN-988, LEN-1010
     * This field is temporary solution specific for allen integration request you to not use this for any other purpose
     */
    private Integer offlineAdvanceEmiTenure;
    private Float minTxnVal;
    private Float maxTxnVal;
    private Float interestWithoutCFee;
    @JsonPOJOBuilder(withPrefix = "")
    public static class PriceResponseBuilder {
    }

}
