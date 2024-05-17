package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentConfigResponse extends BaseResponse {

    private final PaymentConfigInfo paymentConfigInfo;

    @Builder(builderMethodName = "baseResponseBuilder")
    public PaymentConfigResponse(@JsonProperty("code") Integer code,
                                 @JsonProperty("status") String status,
                                 @JsonProperty("message") String message,
                                 @JsonProperty("paymentConfigInfo") PaymentConfigInfo paymentConfigInfo) {
        super(code, status, message);
        this.paymentConfigInfo = paymentConfigInfo;
    }
}