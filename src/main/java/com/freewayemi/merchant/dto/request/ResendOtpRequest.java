package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class ResendOtpRequest {
    private final String id;
    private final String mobile;
}
