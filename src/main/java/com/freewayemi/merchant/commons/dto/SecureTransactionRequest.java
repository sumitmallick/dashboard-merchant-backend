package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SecureTransactionRequest {

    private final String encryptedRequest;
    private final String secretKeyType;

    @JsonCreator
    public SecureTransactionRequest(@JsonProperty("encryptedRequest") String encryptedRequest,
                                    @JsonProperty("secretKeyType") String secretKeyType) {
        this.encryptedRequest = encryptedRequest;
        this.secretKeyType = secretKeyType;
    }

}
