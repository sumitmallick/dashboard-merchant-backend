package com.freewayemi.merchant.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDetails {
    private final String accountNumber;
    private final String ifscCode;
    private final String vpa;
    private final String beneficiaryName;
    private final Boolean isVerified;
}
