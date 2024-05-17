package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigilockerDocumentsRequest {

    @JsonProperty("consent")
    private final String consent;
    @JsonProperty("accessRequestId")
    private final String accessRequestId;
}
