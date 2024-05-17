package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class BankAccVerificationRequest {

    private final String consent;
    private final String ifsc;
    private final String accountNumber;

    public BankAccVerificationRequest(String consent, String ifsc, String accountNumber) {
        this.consent = consent;
        this.ifsc = ifsc;
        this.accountNumber = accountNumber;
    }
}
