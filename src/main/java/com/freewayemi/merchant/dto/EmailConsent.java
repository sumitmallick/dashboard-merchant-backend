package com.freewayemi.merchant.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EmailConsent {
    private Long timestamp;
    private String ipAddress;
    private String receiverEmail;
    private String content;
}
