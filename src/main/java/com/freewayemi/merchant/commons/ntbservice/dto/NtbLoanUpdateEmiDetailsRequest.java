package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanUpdateEmiDetailsRequest {
    private final String prospectId;
    private final String updateType;
    private final String amount;
    private final String emiAmount;
    private final String tenure;
    private final String downpaymentAmount;
    private final String paymentDiscount;
    private final String principal;
    private final String roi;
    private final String transactionId;

    @JsonCreator
    public NtbLoanUpdateEmiDetailsRequest(String prospectId, String updateType, String amount, String emiAmount, String tenure,
                                          String downpaymentAmount, String paymentDiscount, String principal, String roi, String transactionId) {
        this.prospectId = prospectId;
        this.updateType = updateType;
        this.amount = amount;
        this.emiAmount = emiAmount;
        this.tenure = tenure;
        this.downpaymentAmount = downpaymentAmount;
        this.paymentDiscount = paymentDiscount;
        this.principal = principal;
        this.roi = roi;
        this.transactionId = transactionId;
    }
}

