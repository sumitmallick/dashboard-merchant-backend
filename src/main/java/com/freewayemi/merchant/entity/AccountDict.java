package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.karza.BankAccVerificationResponse;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class AccountDict extends BaseEntity {
    private String merchantId;
    private BankAccVerificationResponse pennyDropresult;
    private Instant createdDate;
    private String acc;

    private String ifsc;
}
