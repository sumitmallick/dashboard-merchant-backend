package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = DigilockerLinkResponse.DigilockerLinkResponseBuilder.class)
@Builder(builderClassName = "DigilockerLinkResponseBuilder", toBuilder = true)
public class DigilockerLinkResponse {

    @JsonProperty("result")
    private final Result result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DigilockerLinkResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("link")
        private final String link;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }
}

