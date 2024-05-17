package com.freewayemi.merchant.commons.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.entity.SecurityCredentials;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentConfigInfo {
    private String merchantId;
    private String merchantResponseKey;
    private SecurityCredentials securityCredentials;

    @JsonCreator
    public PaymentConfigInfo(@JsonProperty("merchantId") String merchantId,
                             @JsonProperty("merchantResponseKey") String merchantResponseKey,
                             @JsonProperty("securityCredentials") SecurityCredentials securityCredentials) {
        this.merchantId = merchantId;
        this.merchantResponseKey = merchantResponseKey;
        this.securityCredentials = securityCredentials;
    }
}

