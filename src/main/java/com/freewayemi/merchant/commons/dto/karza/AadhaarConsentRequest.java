package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AadhaarConsentRequest {
    private final String latitude;
    private final String longitude;
    private final String ipAddress;
    private final String userAgent;
    private final String deviceId;
    private final String deviceInfo;
    private final String consent;
    private final String name;
    private final String consentTime;
    private final String consentText;
    private final ClientData clientData;

    @JsonCreator
    public AadhaarConsentRequest(@JsonProperty("lat") String latitude,
                                 @JsonProperty("long") String longitude,
                                 @JsonProperty("ipAddress") String ipAddress,
                                 @JsonProperty("userAgent") String userAgent,
                                 @JsonProperty("deviceId") String deviceId,
                                 @JsonProperty("deviceInfo") String deviceInfo,
                                 @JsonProperty("consent") String consent,
                                 @JsonProperty("name") String name,
                                 @JsonProperty("consentTime") String consentTime,
                                 @JsonProperty("consentText") String consentText,
                                 @JsonProperty("clientData") ClientData clientData) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.consent = consent;
        this.name = name;
        this.consentTime = consentTime;
        this.consentText = consentText;
        this.clientData = clientData;
    }
}
