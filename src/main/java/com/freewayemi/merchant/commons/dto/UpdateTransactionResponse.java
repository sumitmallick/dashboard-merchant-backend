package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTransactionResponse extends BaseResponse {

    @Builder(builderMethodName = "baseResponseBuilder")
    @JsonCreator
    public UpdateTransactionResponse(@JsonProperty("code") Integer code, @JsonProperty("status") String status,
                                     @JsonProperty("message") String message) {
        super(code, status, message);
    }
}
