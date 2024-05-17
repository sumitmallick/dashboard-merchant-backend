package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelLoanRequestDto {

    private String loanId;

    private String transactionId;

    //refund request id
    private String refundId;

    private String refundAmount;

    private String disbursalReferenceNumber;

    private String cancellationReason;

    @JsonCreator
    public CancelLoanRequestDto(@JsonProperty("loanId") String loanId,
                                @JsonProperty("transactionId") String transactionId,
                                @JsonProperty("refundId") String refundId,
                                @JsonProperty("refundAmount") String refundAmount,
                                @JsonProperty("disbursalReferenceNumber") String disbursalReferenceNumber,
                                @JsonProperty("cancellationReason") String cancellationReason) {
        this.loanId = loanId;
        this.transactionId = transactionId;
        this.refundId = refundId;
        this.refundAmount = refundAmount;
        this.disbursalReferenceNumber = disbursalReferenceNumber;
        this.cancellationReason = cancellationReason;
    }
}
