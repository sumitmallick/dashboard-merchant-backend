package com.freewayemi.merchant.commons.dto.qr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QRRequest {
    private final String type;
    private final String uuid;

    @JsonCreator
    public QRRequest(@JsonProperty("type") String type,
                     @JsonProperty("uuid") String uuid) {
        this.type = type;
        this.uuid = uuid;
    }
}
