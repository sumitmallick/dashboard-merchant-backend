package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.type.AppType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "notification_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplate extends BaseEntity {

    private String eventName;
    private Boolean active;
    private List<String> channels;
    private String type;
    private AppType appType;
    private List<String> groups;
    private Map<String, Object> data;
    private String condition;
}
