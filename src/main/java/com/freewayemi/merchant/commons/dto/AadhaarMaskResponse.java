package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AadhaarMaskResponse {
    private Integer code;
    private String status;
    private String message;

    @JsonCreator
    public AadhaarMaskResponse(@JsonProperty("code") Integer code, @JsonProperty("status") String status,
                               @JsonProperty("message") String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
