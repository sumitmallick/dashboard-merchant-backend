package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = NameCheckResponse.NameCheckResponseBuilder.class)
@Builder(builderClassName = "NameCheckResponseBuilder", toBuilder = true)
public class NameCheckResponse {

    @JsonProperty("result")
    private final Result result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    Instant createdDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class NameCheckResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("score")
        private final Double score;

        @JsonProperty("result")
        private final Boolean result;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }
}
