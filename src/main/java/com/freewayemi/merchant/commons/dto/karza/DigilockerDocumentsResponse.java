package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = DigilockerDocumentsResponse.DigilockerDocumentsResponseBuilder.class)
@Builder(builderClassName = "DigilockerDocumentsResponseBuilder", toBuilder = true)
public class DigilockerDocumentsResponse {

    @JsonProperty("result")
    private final Result[] result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DigilockerDocumentsResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("name")
        private final String name;

        @JsonProperty("mimes")
        private final String[] mimes;

        @JsonProperty("issuerId")
        private final String issuerId;

        @JsonProperty("description")
        private final String description;

        @JsonProperty("doctype")
        private final String doctype;

        @JsonProperty("uri")
        private final String uri;

        @JsonProperty("date")
        private final String date;

        @JsonProperty("issuer")
        private final String issuer;

        @JsonProperty("isParseable")
        private final Boolean isParseable;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }
}
