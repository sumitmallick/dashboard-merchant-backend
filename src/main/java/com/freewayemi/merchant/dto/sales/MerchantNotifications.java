package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantNotifications {
    private String id;
    private String leadOwnerId;
    private String merchantId;

    private String title;

    private String body;
    private Boolean readStatus;
    private String eventName;

    private Instant createdDate;
    private Instant lastModifiedDate;
    private Map<String, Object> data;
}
