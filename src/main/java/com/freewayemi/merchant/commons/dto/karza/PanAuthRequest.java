package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class PanAuthRequest {

    private final String consent;
    private final String pan;

    public PanAuthRequest(String consent, String pan) {
        this.consent = consent;
        this.pan = pan;
    }
}
