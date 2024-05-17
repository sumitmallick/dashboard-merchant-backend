package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanDisbursementRequest {

    private final String amount;
    private final String prospectId;
    private final String tenure;
    private final String processingFee;
    private final String gstOnProcessingFee;
    private final String roi;
    private final String transactionId;

    @JsonCreator
    public NtbLoanDisbursementRequest(String amount, String prospectId, String tenure, String processingFee,
                                      String gstOnProcessingFee, String roi, String transactionId) {
        this.amount = amount;
        this.prospectId = prospectId;
        this.tenure = tenure;
        this.processingFee = processingFee;
        this.gstOnProcessingFee = gstOnProcessingFee;
        this.roi = roi;
        this.transactionId = transactionId;
    }
}
