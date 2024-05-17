package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NtbLoanResponse {
    private String loanId;
    private String transactionId;
    private String consumerId;
    private Boolean isTransactionFlow;
    private String ntbProvider;
    private String currentStage;

    @JsonCreator
    public NtbLoanResponse(@JsonProperty("loanId") String loanId,
                           @JsonProperty("transactionId") String transactionId,
                           @JsonProperty("consumerId") String consumerId,
                           @JsonProperty("isTransactionFlow") Boolean isTransactionFlow,
                           @JsonProperty("ntbProvider") String ntbProvider,
                           @JsonProperty("currentStage") String currentStage){
        this.loanId = loanId;
        this.transactionId = transactionId;
        this.consumerId = consumerId;
        this.isTransactionFlow = isTransactionFlow;
        this.ntbProvider = ntbProvider;
        this.currentStage = currentStage;
    }
}
