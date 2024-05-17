package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatusResponse {
    private Integer code;
    private String status;
    private String message;

    @JsonCreator
    public StatusResponse(@JsonProperty("code") Integer code,
                                @JsonProperty("status") String status,
                                @JsonProperty("message") String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
