package com.freewayemi.merchant.dto.paymentOptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.PriceResponse;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOptionsResponse extends BaseResponse {

    private final Map<String, List<PriceResponse>> cardOffers;

    @Builder(builderMethodName = "baseResponseBuilder")
    @JsonCreator
    public PaymentOptionsResponse(@JsonProperty("code") Integer code, @JsonProperty("status") String status,
                                  @JsonProperty("message") String message,
                                  @JsonProperty("cardOffers") Map<String, List<PriceResponse>> cardOffers) {
        super(code, status, message);
        this.cardOffers = cardOffers;
    }
}
