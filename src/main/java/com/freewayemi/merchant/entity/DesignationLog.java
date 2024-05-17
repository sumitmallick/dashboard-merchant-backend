package com.freewayemi.merchant.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class DesignationLog {
    private String designation;
    private Instant from;
    private Instant to;
    private Boolean isLatest;
}
