package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MandateResponse {
    private String redirectUrl;
    private String mandateId;
    private Map<String, String> params;
    private String code;
    private String message;

    @JsonCreator
    public MandateResponse(@JsonProperty("redirectUrl") String redirectUrl,
                           @JsonProperty("mandateId") String mandateId,
                           @JsonProperty("params") Map<String, String> params,
                           @JsonProperty("code") String code,
                           @JsonProperty("message") String message) {
        this.redirectUrl = redirectUrl;
        this.mandateId = mandateId;
        this.params = params;
        this.code = code;
        this.message = message;
    }
}
