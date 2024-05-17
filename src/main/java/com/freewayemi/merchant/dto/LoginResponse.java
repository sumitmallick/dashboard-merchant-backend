package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String mobile;
    @JsonProperty(value = "otp_flow")
    private Boolean otpFlow;
    private Boolean sotpflow;
    private Integer code;
}
