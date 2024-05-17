package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigilockerDownloadRequest {
    private final String accessRequestId;
    private final String consent;
    private final DigilockerFile[] files;

    @JsonCreator
    public DigilockerDownloadRequest(@JsonProperty("accessRequestId") String accessRequestId,
                                     @JsonProperty("consent") String consent,
                                     @JsonProperty("files") DigilockerFile[] files) {
        this.accessRequestId = accessRequestId;
        this.consent = consent;
        this.files = files;
    }
}
