package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SecurityCredentials {

    private final String secretKey;
    private final String ivKey;
    private final String secretKeyType;
    private final String cipher;
    private final String xApiKey;
    private final String token;

    @JsonCreator
    public SecurityCredentials(@JsonProperty("secretKey") String secretKey, @JsonProperty("ivKey") String ivKey,
                               @JsonProperty("secretKeyType") String secretKeyType, @JsonProperty("token") String token,
                               @JsonProperty("cipher") String cipher, @JsonProperty("xApiKey") String xApiKey) {
        this.secretKey = secretKey;
        this.ivKey = ivKey;
        this.secretKeyType = secretKeyType;
        this.cipher = cipher;
        this.xApiKey = xApiKey;
        this.token = token;
    }

}
