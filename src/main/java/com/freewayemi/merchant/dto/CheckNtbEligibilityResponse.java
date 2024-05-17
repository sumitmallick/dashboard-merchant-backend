package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckNtbEligibilityResponse extends StatusResponse {
    private TransactionResponse data;

    @JsonCreator
    public CheckNtbEligibilityResponse(@JsonProperty("code") Integer code,
                                    @JsonProperty("status") String status,
                                    @JsonProperty("message") String message,
                                    @JsonProperty("data") TransactionResponse data) {
        super(code, status, message);
        this.data = data;
    }
}
