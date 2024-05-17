package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenRequest {
    private final String mobile;
    private final String otp;
    private String source;

    @JsonCreator
    public TokenRequest(@JsonProperty("mobile") String mobile, @JsonProperty("otp") String otp) {
        this.mobile = mobile;
        this.otp = otp;
    }
}
