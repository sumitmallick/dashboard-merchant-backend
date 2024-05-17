package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.type.AppType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileRuleDataRequest {
    private String paymentDeviceId;
    private String mobile;
    private AppType appType;
    private String action;
    private String deviceModel;
    private String deviceManufacture;
    private String osName;
    private String osVersion;
    private String latitude;
    private String longitude;
    private String ipAddress;
    private String subscriberId1;
    private String subscriberId2;
    private String referenceId;
    private String uniqueId;
    private String deviceId;
    private String firstTimeInstall;
    private String macAddress;
    private String buildId;
    private String carrierName;

}
