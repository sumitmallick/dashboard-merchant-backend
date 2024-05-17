package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {

    @JsonProperty(value = "username")
    private String userName;

    private String password;
}
