package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDOResponse extends BaseResponse {

    private String doUrl;

    @JsonCreator
    @Builder(builderMethodName = "baseResponseBuilder")
    public TransactionDOResponse(@JsonProperty("code") Integer code,
                                 @JsonProperty("status") String status,
                                 @JsonProperty("message") String message,
                                 @JsonProperty("doUrl") String doUrl) {
        super(code, status, message);
        this.doUrl = doUrl;
    }
}