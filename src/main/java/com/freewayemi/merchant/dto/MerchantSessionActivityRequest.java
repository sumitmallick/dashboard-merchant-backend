package com.freewayemi.merchant.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class MerchantSessionActivityRequest {
    private String type;
    private String sessionId;
    private String paymentLinkId;
    private Instant lastActivityDate;
}
