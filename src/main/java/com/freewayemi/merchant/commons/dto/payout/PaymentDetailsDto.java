package com.freewayemi.merchant.commons.dto.payout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDetailsDto {

    private final String vpa;
    private final String accountType;
    private final String beneficiaryName;
    private final String accountNumber;
    private final String ifscCode;

}
