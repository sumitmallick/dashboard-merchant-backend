package com.freewayemi.merchant.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import java.util.Map;
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionDataResponse {
    private final String transactionId;
    private final String transactionStatus;
    private final String transactionStatusMessage;
    private final Map<String, String> params;

    @Builder(builderMethodName = "baseResponseBuilder")
    public TransactionDataResponse(
            @JsonProperty("params") Map<String, String> params,
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("transactionStatus") String transactionStatus,
            @JsonProperty("transactionStatusMessage") String transactionStatusMessage) {
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
        this.params = params;
        this.transactionStatusMessage = transactionStatusMessage;
    }
}