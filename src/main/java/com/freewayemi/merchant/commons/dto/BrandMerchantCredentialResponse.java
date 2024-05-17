package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandMerchantCredentialResponse {
    private final Integer code;
    private final String status;
    private final String message;
    private List<BrandMerchantCredentialDTO> merchantCredentials;

    @JsonCreator
    public BrandMerchantCredentialResponse(@JsonProperty(value = "code") Integer code,
                                           @JsonProperty(value = "status") String status,
                                           @JsonProperty(value = "message") String message,
                                           @JsonProperty(value = "merchantCredentials")
                                           List<BrandMerchantCredentialDTO> merchantCredentials) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.merchantCredentials = merchantCredentials;
    }
}
