package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreUserTransaction {
    @JsonProperty("transactionResponses")
    List<TransactionResponse> transactionResponses;


    public StoreUserTransaction(@JsonProperty("transactionResponses") List<TransactionResponse> transactionResponses) {
        this.transactionResponses = transactionResponses;
    }
}
