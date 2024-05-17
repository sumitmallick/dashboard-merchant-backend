package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgTokenRequest {
    private final String clientId;
    private final String clientSecret;
    private String type;

    @JsonCreator
    public PgTokenRequest(@JsonProperty(value = "clientId") String clientId, @JsonProperty(value = "clientSecret") String clientSecret,
                          @JsonProperty(value = "type") String type) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.type = type;
    }
}
