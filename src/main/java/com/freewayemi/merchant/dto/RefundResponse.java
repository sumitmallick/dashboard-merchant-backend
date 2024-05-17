package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.response.EnquiryTransactionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundResponse extends StatusResponse {
    private RefundData data;

    @JsonCreator
    public RefundResponse(@JsonProperty("code") Integer code,
                           @JsonProperty("status") String status,
                           @JsonProperty("message") String message,
                           @JsonProperty("data") RefundData data) {
        super(code, status, message);
        this.data = data;
    }
}