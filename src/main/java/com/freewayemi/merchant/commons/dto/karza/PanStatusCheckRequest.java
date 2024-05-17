package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class PanStatusCheckRequest {

    private final String pan;
    private final String name;
    private final String dob;
    private final String consent;

    public PanStatusCheckRequest(String pan, String name, String dob, String consent) {
        this.pan = pan;
        this.name = name;
        this.dob = dob;
        this.consent = consent;
    }
}
