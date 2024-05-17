package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceIdResponse extends BaseResponse {

    public String deviceId;

    @Builder(builderMethodName = "basicResponseBuilder")
    public DeviceIdResponse(@JsonProperty("code") Integer code,
                            @JsonProperty("status") String status,
                            @JsonProperty("message") String message,
                            @JsonProperty("deviceId") String deviceId) {
        super(code, message, status);
        this.deviceId = deviceId;
    }
}
