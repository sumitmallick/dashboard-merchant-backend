package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class GstAuthenticationRequest {
    private final String consent;
    private final Boolean additionalData;
    private final String gstin;

    public GstAuthenticationRequest(String consent, Boolean additionalData, String gstin) {
        this.consent = consent;
        this.additionalData = additionalData;
        this.gstin = gstin;
    }
}
