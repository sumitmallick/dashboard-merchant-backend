package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NTBLoanInfoDTO {

    private final String loanId;
    private final String loanStatus;
    private final String transactionId;
    private final String consumerId;
    private final Map<String, String> additionInfo;
    private final Boolean isTransactionFlow;
    private final String transactionCallbackUrl;
    private final Boolean isLoanExpired;
    private final Boolean isLoanRejected;
    private final Boolean isLoanDisbursed;
    private final String disbursalReferenceNumber;

    // To accept Error
    private final String message;
    private final String code;

    @JsonCreator
    public NTBLoanInfoDTO(@JsonProperty(value = "loanId") String loanId, @JsonProperty(value = "loanStatus") String loanStatus,
                          @JsonProperty(value = "transactionId") String transactionId, @JsonProperty(value = "consumerId") String consumerId,
                          @JsonProperty(value = "additionInfo") Map<String, String> additionInfo,
                          @JsonProperty(value = "isTransactionFlow") Boolean isTransactionFlow,
                          @JsonProperty(value = "transactionCallbackUrl") String transactionCallbackUrl,
                          @JsonProperty(value = "isLoanExpired") Boolean isLoanExpired,
                          @JsonProperty(value = "isLoanRejected") Boolean isLoanRejected,
                          @JsonProperty(value = "isLoanDisbursed") Boolean isLoanDisbursed,
                          @JsonProperty(value = "disbursalReferenceNumber") String disbursalReferenceNumber,
                          @JsonProperty(value = "message") String message, @JsonProperty(value = "code") String code) {
        this.loanId = loanId;
        this.loanStatus = loanStatus;
        this.transactionId = transactionId;
        this.consumerId = consumerId;
        this.isTransactionFlow = isTransactionFlow;
        this.transactionCallbackUrl = transactionCallbackUrl;
        this.isLoanExpired = isLoanExpired;
        this.isLoanRejected = isLoanRejected;
        this.isLoanDisbursed = isLoanDisbursed;
        this.disbursalReferenceNumber = disbursalReferenceNumber;
        this.message = message;
        this.code = code;
        this.additionInfo = additionInfo;
    }
}
