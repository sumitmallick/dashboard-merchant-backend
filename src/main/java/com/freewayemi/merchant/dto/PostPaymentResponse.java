package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.*;

import java.util.Map;
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostPaymentResponse extends BaseResponse {
    private final TransactionDataResponse data;
    @Builder(builderMethodName = "baseResponseBuilder")
    public PostPaymentResponse(@JsonProperty("code") Integer code,
                               @JsonProperty("status") String status,
                               @JsonProperty("message") String message,
                               @JsonProperty("transactionId") TransactionDataResponse data) {
        super(code, status, message);
        this.data = data;
    }
}