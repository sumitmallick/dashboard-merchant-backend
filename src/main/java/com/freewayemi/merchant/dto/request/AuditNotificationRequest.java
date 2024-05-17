package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class AuditNotificationRequest {
    private String channel;
    private String name;
    private Map<String, Object> destination;
    private Map<String, Object> message;
    private String receiver;
    private String receiverId;
    private Instant createdDate;
}
