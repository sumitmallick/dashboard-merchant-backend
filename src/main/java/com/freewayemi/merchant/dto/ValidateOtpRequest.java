package com.freewayemi.merchant.dto;

import lombok.Data;

@Data
public class ValidateOtpRequest {
    private String otp;
    private String action;
}
