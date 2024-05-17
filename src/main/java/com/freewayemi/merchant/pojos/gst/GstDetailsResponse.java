package com.freewayemi.merchant.pojos.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GstDetailsResponse {

    @JsonProperty(value = "code")
    private final Integer code;
    @JsonProperty(value = "status")
    private final String status;
    @JsonProperty(value = "statusMessage")
    private final String statusMessage;
    @JsonProperty(value = "gstData")
    private GSTData gstData;

    @JsonCreator
    public GstDetailsResponse(@JsonProperty(value = "code") Integer code,
                              @JsonProperty(value = "status") String status,
                              @JsonProperty(value = "statusMessage") String statusMessage,
                              @JsonProperty(value = "gstData") GSTData gstData) {
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.gstData = gstData;
    }
}
