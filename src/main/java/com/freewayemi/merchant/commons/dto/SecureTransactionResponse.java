package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecureTransactionResponse {

    private final String encryptedResponse;

    @JsonCreator
    public SecureTransactionResponse(@JsonProperty("encryptedResponse") String encryptedResponse) {
        this.encryptedResponse = encryptedResponse;
    }

}
