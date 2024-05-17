package com.freewayemi.merchant.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MobileConsent {
    private Long timestamp;

    private String ipAddress;
    private String receiverMobile;
    private String content;
}
