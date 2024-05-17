package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanUpdateEmiDetailsResponse {
    private final String code;
    private final String message;

    @JsonCreator
    public NtbLoanUpdateEmiDetailsResponse(@JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }
}
