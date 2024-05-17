package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = TransactionSettlementSummaryResponse.TransactionSettlementsResponseBuilder.class)
@Builder(builderClassName = "TransactionSettlementsResponseBuilder", toBuilder = true)
public class TransactionSettlementSummaryResponse {
    private Float amount;
    private List<TransactionSettlementResponse> transactions;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TransactionSettlementsResponseBuilder {
    }

    public void addToSummary(TransactionSettlementResponse tsr) {
        this.amount = this.amount + Float.parseFloat(tsr.getNetSettlementAmt());
        this.transactions.add(tsr);
    }
}
