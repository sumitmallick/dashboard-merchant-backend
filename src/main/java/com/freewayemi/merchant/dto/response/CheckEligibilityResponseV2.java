package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = CheckEligibilityResponseV2.CheckEligibilityResponseV2Builder.class)
@Builder(builderClassName = "CheckEligibilityResponseV2Builder", toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckEligibilityResponseV2 {
    private final String instantEmiEligibility;
    private final String ccEmiAvailability;
    private final List<EligibilityDetailV2> eligibilityDetails;
    private final Integer code;
    private final String message;

    private final String preApprovedEligibility;
    private final String ntbEligibility;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CheckEligibilityResponseV2Builder {
    }

}
