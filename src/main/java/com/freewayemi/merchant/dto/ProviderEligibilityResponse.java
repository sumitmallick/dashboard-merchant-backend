package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderEligibilityResponse extends StatusResponse {
    private String creditType;
    private List<ProviderEligibilityApiResponse> eligibilities;
    private Long pollingIntervalInSeconds;

    @JsonCreator
    public ProviderEligibilityResponse(@JsonProperty("code") Integer code,
                                       @JsonProperty("status") String status,
                                       @JsonProperty("message") String message,
                                       @JsonProperty("creditType") String creditType,
                                       @JsonProperty("eligibilities") List<ProviderEligibilityApiResponse> eligibilities,
                                       @JsonProperty("pollingIntervalInSeconds") Long pollingIntervalInSeconds) {
        super(code, status, message);
        this.creditType = creditType;
        this.eligibilities = eligibilities;
        this.pollingIntervalInSeconds = pollingIntervalInSeconds;
    }
}
