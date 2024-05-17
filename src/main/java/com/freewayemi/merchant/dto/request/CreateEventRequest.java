package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CreateEventRequest {
    private String eventName;
    private String value;
    private String createdBy;
    private String createdByType;
    private String merchantId;
    private Instant createdDate;
    private String reason;
    private String storeUserId;
}
