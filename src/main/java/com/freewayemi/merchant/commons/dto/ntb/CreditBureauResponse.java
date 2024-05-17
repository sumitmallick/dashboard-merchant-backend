package com.freewayemi.merchant.commons.dto.ntb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreditBureauResponse {

    private final String code;
    private final String status;
    private final String message;
    private String authType;
    private String authSubType;
    private String authRequest;
    private List<String> authOptions;

    @JsonCreator
    public CreditBureauResponse(@JsonProperty("code") String code, @JsonProperty("status") String status,
                                        @JsonProperty("message") String message, @JsonProperty("authType") String authType,
                                        @JsonProperty("authSubType") String authSubType, @JsonProperty("authRequest") String authRequest,
                                        @JsonProperty("authOptions") List<String> authOptions) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.authType = authType;
        this.authSubType = authSubType;
        this.authRequest = authRequest;
        this.authOptions = authOptions;
    }
}
