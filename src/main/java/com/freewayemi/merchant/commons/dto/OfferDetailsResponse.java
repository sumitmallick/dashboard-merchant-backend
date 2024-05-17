package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OfferDetailsResponse {
    Float noCostDiscount;
    Float additionalDiscount;
    Float additionalCashback;
    Float cashbackRate;
    Float cashback;
    Boolean instantEmiDiscount;
    Integer effectiveTenure;
    List<OfferDetails> offerDetailsList;
    Float advanceDownPaymentRate;
    Integer offlineAdvanceEmiTenure;

    // margin money fields
    Float transactionAmount;
    Float irrpa;
    Float downPaymentAmount;
    String downPaymentType;
    Float minMarginDownPaymentAmount;
    Float maxMarginDownPaymentAmount;
    Float discountRate;

    @JsonCreator
    public OfferDetailsResponse(@JsonProperty Float noCostDiscount,
                                @JsonProperty Float additionalDiscount,
                                @JsonProperty Float additionalCashback,
                                @JsonProperty Float cashbackRate,
                                @JsonProperty Float cashback,
                                @JsonProperty Boolean instantEmiDiscount,
                                @JsonProperty Integer effectiveTenure,
                                @JsonProperty List<OfferDetails> offerDetailsList,
                                @JsonProperty Float advanceDownPaymentRate,
                                @JsonProperty Integer offlineAdvanceEmiTenure,
                                @JsonProperty Float transactionAmount,
                                @JsonProperty Float irrpa,
                                @JsonProperty Float downPaymentAmount,
                                @JsonProperty String downPaymentType,
                                @JsonProperty Float minMarginDownPaymentAmount,
                                @JsonProperty Float maxMarginDownPaymentAmount,
                                @JsonProperty Float discountRate){
        this.noCostDiscount = noCostDiscount;
        this.additionalDiscount = additionalDiscount;
        this.additionalCashback = additionalCashback;
        this.cashbackRate = cashbackRate;
        this.cashback = cashback;
        this.instantEmiDiscount = instantEmiDiscount;
        this.effectiveTenure = effectiveTenure;
        this.offerDetailsList = offerDetailsList;
        this.advanceDownPaymentRate = advanceDownPaymentRate;
        this.offlineAdvanceEmiTenure = offlineAdvanceEmiTenure;
        this.transactionAmount = transactionAmount;
        this.irrpa = irrpa;
        this.downPaymentAmount = downPaymentAmount;
        this.downPaymentType = downPaymentType;
        this.minMarginDownPaymentAmount = minMarginDownPaymentAmount;
        this.maxMarginDownPaymentAmount = maxMarginDownPaymentAmount;
        this.discountRate = discountRate;
    }
}
