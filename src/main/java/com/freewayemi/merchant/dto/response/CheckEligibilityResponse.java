package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = CheckEligibilityResponse.CheckEligibilityResponseBuilder.class)
@Builder(builderClassName = "CheckEligibilityResponseBuilder", toBuilder = true)
public class CheckEligibilityResponse {

    private final String instantEmiEligibility;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String ccEmiAvailability;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<EligibilityDetail> eligibilityDetails;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Integer code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String paymentRefNo;

    private final String preApprovedEligibility;
    
    private final String ntbEligibility;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CheckEligibilityResponseBuilder {
    }

}
