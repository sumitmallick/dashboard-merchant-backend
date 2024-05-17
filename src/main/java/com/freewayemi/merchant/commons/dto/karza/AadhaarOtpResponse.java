package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = AadhaarOtpResponse.AadhaarOtpResponseBuilder.class)
@Builder(builderClassName = "AadhaarOtpResponseBuilder", toBuilder = true)
public class AadhaarOtpResponse {

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonProperty("clientData")
    private final ClientData clientData;

    @JsonProperty("result")
    private final AadhaarConsentResponse.Result result;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AadhaarOtpResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("message")
        private final String message;

        @JsonProperty("accessKey")
        private final String accessKey;

        @JsonProperty("accessKeyValidity")
        private final String accessKeyValidity;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }

        @Override
        public String toString() {
            return "Result{" +
                    "message='" + message + '\'' +
                    ", accessKey='" + Util.truncateString(accessKey) + '\'' +
                    ", accessKeyValidity='" + accessKeyValidity + '\'' +
                    '}';
        }
    }

}
