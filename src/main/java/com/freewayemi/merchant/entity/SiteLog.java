package com.freewayemi.merchant.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class SiteLog {
    private String city;
    private String state;
    private String region;
    private Instant createdDate;
    private String createdBy;
}
