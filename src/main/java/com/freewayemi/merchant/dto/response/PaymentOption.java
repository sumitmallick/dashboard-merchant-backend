package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = PaymentOption.PaymentOptionBuilder.class)
@Builder(builderClassName = "PaymentOptionBuilder", toBuilder = true)
public class PaymentOption {

    private final int tenure;
    private final Float interestRate;
    private final Float emiAmount;
    private final Float totalPayableAmount;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PaymentOptionBuilder {
    }
}
