package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "notifications")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    private String leadOwnerId;

    private String title;

    private String body;
    private List<String> groups;
    private Boolean readStatus;
    private String merchantId;
    private String source;
    private String eventName;
    private Boolean active;
    private Map<String, Object> data;
    private Map<String, Object> channelsInfo;
    private Map<String, Object> otherData;
    private Boolean sentNotification;
}
