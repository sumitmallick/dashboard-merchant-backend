package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AadhaarFileRequest {
    private final String consent;
    private final String otp;
    private final String accessKey;
    private final ClientData clientData;
    private final String shareCode;

    @JsonCreator
    public AadhaarFileRequest(@JsonProperty("consent") String consent,
                             @JsonProperty("otp") String otp,
                             @JsonProperty("accessKey") String accessKey,
                             @JsonProperty("clientData") ClientData clientData,
                              @JsonProperty("shareCode") String shareCode) {
        this.consent = consent;
        this.otp = otp;
        this.accessKey = accessKey;
        this.clientData = clientData;
        this.shareCode = shareCode;
    }

    @Override
    public String toString() {
        return "AadhaarFileRequest{" +
                "consent='" + consent + '\'' +
                ", otp='" + otp + '\'' +
                ", accessKey='" + Util.truncateString(accessKey) + '\'' +
                ", clientData=" + clientData +
                ", shareCode='" + shareCode + '\'' +
                '}';
    }
}
