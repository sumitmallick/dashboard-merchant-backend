package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@JsonDeserialize(builder = SettlementResponse.SettlementResponseBuilder.class)
@Builder(builderClassName = "SettlementResponseBuilder", toBuilder = true)
@Data
public class SettlementResponse {
    private Float pendingAmount;
    private Float totalAmount;
    private Integer txnCnt;
    private Map<LocalDate, TransactionSettlementSummaryResponse> settledTransactions;

    @JsonPOJOBuilder(withPrefix = "")
    public static class SettlementResponseBuilder {
    }

}