package com.freewayemi.merchant.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class NotificationEvent {

    private String eventName;
    private String merchantId;
    private String displayId;
    private Map<String, Object> eventData;
}
