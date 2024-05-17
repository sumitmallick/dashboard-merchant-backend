package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.dto.karza.BankAccVerificationResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AccountDict {
    private String merchantId;

    private BankAccVerificationResponse pennyDropresult;
    private Instant createdDate;
    private String acc;

    private String ifsc;
}
