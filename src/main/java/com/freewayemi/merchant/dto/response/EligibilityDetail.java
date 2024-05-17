package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.dto.EligibileBandInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = EligibilityDetail.EligibilityDetailBuilder.class)
@Builder(builderClassName = "EligibilityDetailBuilder", toBuilder = true)
public class EligibilityDetail {
    private final String bankCode;
    private final String cardType;
    private final String instantEmiEligibility;
    private final List<PaymentOption> paymentOptions;

    private final String preApprovedEligibility;

    private final String ntbEligibility;
    private final EligibileBandInfo bandInfo;

    @JsonPOJOBuilder(withPrefix = "")
    public static class EligibilityDetailBuilder {
    }
}
