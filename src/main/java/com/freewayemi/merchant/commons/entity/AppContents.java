package com.freewayemi.merchant.commons.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "app_contents")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppContents extends BaseEntity {
    private String type;
    private String landing;
    private String title;
    private String text;
    private String subText;
    private String icon;
    private Boolean active;
    private String contentType;
    private List<String> userStatus;
    private List<String> category;
    private String merchantName;
    private String url;
    private Integer order;
    private String screen;
    private String entityId;
    private Integer minAndroidVersion;
    private Instant expiry;
    private List<String> brandIds;
}