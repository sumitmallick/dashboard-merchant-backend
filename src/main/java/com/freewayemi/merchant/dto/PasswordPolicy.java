package com.freewayemi.merchant.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class PasswordPolicy {
    private Integer invalidAttempts;
    private Instant lastPwdModifiedDate;
    private Instant accountLockedAt;
    private List<Password> passwords;
}
