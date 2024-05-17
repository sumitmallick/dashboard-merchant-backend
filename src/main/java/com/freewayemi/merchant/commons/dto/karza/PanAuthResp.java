package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PanAuthResp {
    private String statusCode;
    private String statusMessage;
    private String pan;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PanAuthResponse panAuthResponse;

    @JsonCreator
    public PanAuthResp(@JsonProperty("statusCode") String statusCode,
                       @JsonProperty("statusMessage") String statusMessage,
                       @JsonProperty("pan") String pan,
                       @JsonProperty("name") String name,
                       @JsonProperty("panAuthResponse") PanAuthResponse panAuthResponse) {
        this.statusCode = statusCode;
        this.pan = pan;
        this.statusMessage = statusMessage;
        this.panAuthResponse = panAuthResponse;
        this.name = name;
    }
}
