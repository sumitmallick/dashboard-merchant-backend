package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class FaceMatchingRequest {
    private final String image1B64;
    private final String image2B64;

    public FaceMatchingRequest(String image1B64, String image2B64) {
        this.image1B64 = image1B64;
        this.image2B64 = image2B64;
    }
}
