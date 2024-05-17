package com.freewayemi.merchant.commons.dto.ntb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.karza.DigilockerDownloadResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigilockerDataResp {
    private String status;
    private String statusMsg;
    private String postRedirectionUrl;
    private DigilockerDownloadResponse digilockerDownloadResponse;

    @JsonCreator
    public DigilockerDataResp(@JsonProperty("status") String status,
                              @JsonProperty("statusMsg") String statusMsg,
                              @JsonProperty("postRedirectionUrl") String postRedirectionUrl,
                              @JsonProperty("digilockerDownloadResponse")
                                          DigilockerDownloadResponse digilockerDownloadResponse) {
        this.status = status;
        this.statusMsg = statusMsg;
        this.postRedirectionUrl = postRedirectionUrl;
        this.digilockerDownloadResponse = digilockerDownloadResponse;
    }
}
