package com.freewayemi.merchant.pojos;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ResolvedNotification {

    private Map<String, Object> channelsInfo;
    private Map<String, Object> data;
    private String merchantId;
    private String leadOwnerId;
    private boolean resolveSuccess;
    private boolean sendNotification;
}
