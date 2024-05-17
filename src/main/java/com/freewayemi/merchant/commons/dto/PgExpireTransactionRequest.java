package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgExpireTransactionRequest {
    private final String source;
    private final String transactionId;
    private final String cardType;
    private final String transactionStatus;
    private final String currentTransactionStatus;

    @JsonCreator
    public PgExpireTransactionRequest(@JsonProperty("source") String source,
                                @JsonProperty("transactionId") String transactionId,
                                @JsonProperty("cardType") String cardType,
                                @JsonProperty("transactionStatus") String transactionStatus,
                                @JsonProperty("currentTransactionStatus") String currentTransactionStatus) {
        this.source = source;
        this.transactionId = transactionId;
        this.cardType = cardType;
        this.transactionStatus = transactionStatus;
        this.currentTransactionStatus = currentTransactionStatus;
    }
}