package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantApiCredentialsDto {
    private String token;
    private String secretKey;
    private String ivKey;
    private String merchantResponseKey;

    @JsonCreator
    public MerchantApiCredentialsDto(@JsonProperty(value = "token") String token,
                                     @JsonProperty(value = "secretKey") String secretKey,
                                     @JsonProperty(value = "ivKey") String ivKey,
                                     @JsonProperty(value = "merchantResponseKey") String merchantResponseKey) {
        this.token = token;
        this.secretKey = secretKey;
        this.ivKey = ivKey;
        this.merchantResponseKey = merchantResponseKey;
    }
}
