package com.freewayemi.merchant.commons.dto.karza;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AadhaarXmlRequestDto {
    private final String userId;
    private final String userLatitude;
    private final String userLongitude;
    private final String userIpAddress;
    private final String userAgent;
    private final String userName;
    private final String consentText;
    private final String accessKey;
    private final String aadhaarNumber;
    private final String otp;
    private final String shareCode;
}
