package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankDetailsResponse extends BaseResponse {

    private Map<String, BankEnumDetails> bankEnumDetailsMap;

    @JsonCreator
    @Builder(builderMethodName = "baseResponseBuilder")
    public BankDetailsResponse(@JsonProperty("code") Integer code,
                               @JsonProperty("status") String status,
                               @JsonProperty("message") String message,
                               @JsonProperty("bankEnumDetailsMap") Map<String, BankEnumDetails> bankEnumDetailsMap) {
        super(code, status, message);
        this.bankEnumDetailsMap = bankEnumDetailsMap;
    }
}
