package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "merchant_nudges")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantNudge extends BaseEntity {
    private String merchantId;
    private String title;
    private String type;
    private String icon;
    private String text;
    private String action;
    private String subText;
    private Boolean isRead;
    private Instant readAt;
    private String nudgeId;
    private Integer count;
}
