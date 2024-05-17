package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private Integer pollingInterval;

    @JsonCreator
    public TransactionResponse(@JsonProperty("transactionId") String transactionId,
                               @JsonProperty("pollingInterval") Integer pollingInterval){
        this.transactionId = transactionId;
        this.pollingInterval = pollingInterval;
    }
}
