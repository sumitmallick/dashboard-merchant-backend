package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = PaymentLinkResponse.PaymentLinkResponseBuilder.class)
@Builder(builderClassName = "PaymentLinkResponseBuilder", toBuilder = true)
public class PaymentLinkResponse {

    private final Float amount;
    private final String orderId;
    private final String email;
    private final String mobile;
    private final Instant expiryDate;
    private final String status;
    private final String emailPaymentLink;
    private final Integer code;
    private final String message;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PaymentLinkResponseBuilder {
    }

}
