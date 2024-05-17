package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderEligibilityRes{
    private Integer code;
    private String status;
    private String message;
    private ProviderEligibility data;
}
