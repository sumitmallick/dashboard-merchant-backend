package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = PanStatusCheckResponse.PanStatusCheckResponseBuilder.class)
@Builder(builderClassName = "PanStatusCheckResponseBuilder", toBuilder = true)
public class PanStatusCheckResponse {

    @JsonProperty("result")
    private final Result result;

    @JsonProperty("request_id")
    private final String requestId;

    @JsonProperty("status-code")
    private final String statusCode;

    Instant createdDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PanStatusCheckResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("status")
        private final String status;

        @JsonProperty("duplicate")
        private final String duplicate;

        @JsonProperty("nameMatch")
        private final Boolean nameMatch;

        @JsonProperty("dobMatch")
        private final Boolean dobMatch;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }
}
