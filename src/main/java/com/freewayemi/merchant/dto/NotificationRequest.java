package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationRequest {
    public String leadOwnerId;
    public String title;
    public String body;
    public String merchantId;
    public String source;
    public Instant createdDate;
    public Boolean readStatus;
}
