package com.freewayemi.merchant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class Consent {
    private Long timestamp;
    private String ipAddress;
    private List<ProviderConsent> providerConsents;
}
