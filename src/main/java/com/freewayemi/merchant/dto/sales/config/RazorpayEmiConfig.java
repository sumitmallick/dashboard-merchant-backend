package com.freewayemi.merchant.dto.sales.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RazorpayEmiConfig {
    private String merchantKeyId;
    private String merchantKeySecret;
    private Boolean isRedirectEnabled;
}
