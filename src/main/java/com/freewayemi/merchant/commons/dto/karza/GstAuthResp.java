package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GstAuthResp {
    private String statusCode;
    private String statusMessage;
    private String gst;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GstAuthenticationResponse gstAuthenticationResponse;

    @JsonCreator
    public GstAuthResp(@JsonProperty("statusCode") String statusCode,
                       @JsonProperty("statusMessage") String statusMessage,
                       @JsonProperty("gst") String gst,
                       @JsonProperty("gstAuthenticationResponse") GstAuthenticationResponse gstAuthenticationResponse) {
        this.statusCode = statusCode;
        this.gst = gst;
        this.statusMessage = statusMessage;
        this.gstAuthenticationResponse = gstAuthenticationResponse;
    }
}
