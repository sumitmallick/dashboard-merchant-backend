package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.Map;
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentProviderTransactionResponseV2{
    private Integer code;
    private String status;
    private String statusMessage;
    private final TransactionDataResponse data;
    @JsonCreator
    @Builder(builderMethodName = "baseResponseBuilder")
    public PaymentProviderTransactionResponseV2(Integer code, String status, String statusMessage, TransactionDataResponse data) {
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.data = data;
    }
}