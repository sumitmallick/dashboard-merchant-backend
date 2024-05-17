package com.freewayemi.merchant.commons.dto.deliveryorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChargeInfo {

    private final String tenure;
    private final String transactionAmount;
    private final String downPaymentAmount;
    private final String netDisbursementAmount;
    private final String processingFee;
    private final String otherCharges;
    private final String insurancePremiumAmount;
    private final String extendedWarrantyAmount;
    private final String interestAmount;
    private final String paymentAssistanceAmount;
    private final String advanceEmiAmount;
    private final String emiAmount;
    private Float interestWithoutCFee;

    @JsonCreator
    public ChargeInfo(@JsonProperty("statusCode") String tenure,
                      @JsonProperty("transactionAmount") String transactionAmount,
                      @JsonProperty("downPaymentAmount") String downPaymentAmount,
                      @JsonProperty("netDisbursementAmount") String netDisbursementAmount,
                      @JsonProperty("processingFee") String processingFee,
                      @JsonProperty("otherCharges") String otherCharges,
                      @JsonProperty("insurancePremiumAmount") String insurancePremiumAmount,
                      @JsonProperty("extendedWarrantyAmount") String extendedWarrantyAmount,
                      @JsonProperty("interestAmount") String interestAmount,
                      @JsonProperty("paymentAssistanceAmount") String paymentAssistanceAmount,
                      @JsonProperty("advanceEmiAmount") String advanceEmiAmount,
                      @JsonProperty("emiAmount") String emiAmount,
                      @JsonProperty("interestWithoutCFee") Float interestWithoutCFee) {
        this.tenure = tenure;
        this.transactionAmount = transactionAmount;
        this.downPaymentAmount = downPaymentAmount;
        this.netDisbursementAmount = netDisbursementAmount;
        this.processingFee = processingFee;
        this.otherCharges = otherCharges;
        this.insurancePremiumAmount = insurancePremiumAmount;
        this.extendedWarrantyAmount = extendedWarrantyAmount;
        this.interestAmount = interestAmount;
        this.paymentAssistanceAmount = paymentAssistanceAmount;
        this.advanceEmiAmount = advanceEmiAmount;
        this.emiAmount = emiAmount;
        this.interestWithoutCFee = interestWithoutCFee;
    }

}
