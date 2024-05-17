package com.freewayemi.merchant.pojos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse {
    @JsonProperty(value = "code")
    private final Integer code;
    @JsonProperty(value = "status")
    private final String status;
    @JsonProperty(value = "statusMessage")
    private final String statusMessage;

    @JsonProperty(value = "data")
    private final MerchantUserResponse data;

    @JsonCreator
    public APIResponse(@JsonProperty(value = "code") Integer code,
                       @JsonProperty(value = "status") String status,
                       @JsonProperty(value = "statusMessage") String statusMessage,
                       @JsonProperty(value = "data") MerchantUserResponse data) {
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.data = data;
    }
}
