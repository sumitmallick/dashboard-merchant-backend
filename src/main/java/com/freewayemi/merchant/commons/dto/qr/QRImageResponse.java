package com.freewayemi.merchant.commons.dto.qr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QRImageResponse {
    private final String image;

    @JsonCreator
    public QRImageResponse(@JsonProperty("image") String image) {
        this.image = image;
    }
}
