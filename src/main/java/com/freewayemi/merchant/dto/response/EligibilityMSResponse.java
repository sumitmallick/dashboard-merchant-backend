package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EligibilityMSResponse {

    private final Integer code;
    private final String status;
    private final String message;
    private final List<EligibilityResponse> eligibilities;

    @JsonCreator
    public EligibilityMSResponse(@JsonProperty("code") Integer code, @JsonProperty("status") String status,
                                 @JsonProperty("message") String message,
                                 @JsonProperty("eligibilities") List<EligibilityResponse> eligibilities) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.eligibilities = eligibilities;
    }
}
