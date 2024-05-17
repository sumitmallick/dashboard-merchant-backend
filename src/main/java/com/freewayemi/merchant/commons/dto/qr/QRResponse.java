package com.freewayemi.merchant.commons.dto.qr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QRResponse {
    private final String type;
    private final String uuid;
    private final String smsCode;

    @JsonCreator
    public QRResponse(@JsonProperty("type") String type,
                      @JsonProperty("uuid") String uuid,
                      @JsonProperty("smsCode") String smsCode) {
        this.type = type;
        this.uuid = uuid;
        this.smsCode = smsCode;
    }
}
