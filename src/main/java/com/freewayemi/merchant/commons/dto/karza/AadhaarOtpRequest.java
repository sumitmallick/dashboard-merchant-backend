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
public class AadhaarOtpRequest {
    private final String consent;
    private final String aadhaarNo;
    private final String accessKey;
    private final ClientData clientData;

    @JsonCreator
    public AadhaarOtpRequest(@JsonProperty("consent") String consent,
                             @JsonProperty("aadhaarNo") String aadhaarNo,
                             @JsonProperty("accessKey") String accessKey,
                             @JsonProperty("clientData") ClientData clientData) {
        this.consent = consent;
        this.aadhaarNo = aadhaarNo;
        this.accessKey = accessKey;
        this.clientData = clientData;
    }

    @Override
    public String toString() {
        return "AadhaarOtpRequest{" +
                "consent='" + consent + '\'' +
                ", aadhaarNo='" + Util.truncateString(aadhaarNo) + '\'' +
                ", accessKey='" + Util.truncateString(accessKey) + '\'' +
                ", clientData=" + clientData +
                '}';
    }
}
