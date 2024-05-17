package com.freewayemi.merchant.commons.dto.karza;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccVerificationDto {
    private final String ifsc;
    private final String accountNumber;
    private final String source;
    private final String merchantId;
    private final String consumerId;
}
