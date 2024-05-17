package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponse {
    private final String token;

    @JsonCreator
    public TokenResponse(@JsonProperty("token") String token) {
        this.token = token;
    }
}
