package com.freewayemi.merchant.dto.response;

import lombok.Data;

@Data
public class OtpResponse {
    public final boolean isValid;
    public final String message;
}
