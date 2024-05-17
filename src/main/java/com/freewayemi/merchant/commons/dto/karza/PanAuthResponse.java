package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PanAuthResponse {
    private final String statusCode;
    private final String requestId;
    private final Result result;
    private final String error;
    Instant createdDate;

    @JsonCreator
    public PanAuthResponse(@JsonProperty("result") Result result,
                                       @JsonProperty("error") String error,
                                       @JsonProperty("status-code") String statusCode,
                                       @JsonProperty("request_id") String requestId) {
        this.result = result;
        this.error = error;
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.createdDate = Instant.now();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private final String name;

        @JsonCreator
        public Result(@JsonProperty("name") String name) {
            this.name = name;
        }
    }
}
