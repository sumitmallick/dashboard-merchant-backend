package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = FaceMatchingResponse.FaceMatchingResponseBuilder.class)
@Builder(builderClassName = "FaceMatchingResponseBuilder", toBuilder = true)
public class FaceMatchingResponse {

    @JsonProperty("result")
    private final Result result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    Instant createdDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class FaceMatchingResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("matchScore")
        private final Double matchScore;

        @JsonProperty("match")
        private final String match;

        @JsonProperty("confidence")
        private final Double confidence;

        @JsonProperty("reviewNeeded")
        private final String reviewNeeded;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }

}
