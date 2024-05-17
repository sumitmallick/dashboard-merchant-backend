package com.freewayemi.merchant.commons.dto;

import com.freewayemi.merchant.commons.dto.karza.BankAccVerificationResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConsumerAccountAuthResponse {
    private String statusCode;
    private String statusMessage;
    private String accountNumber;
    private String ifsc;
    private BankAccVerificationResponse bankAccVerificationResponse;
    private BankDetails bankDetails;
}
