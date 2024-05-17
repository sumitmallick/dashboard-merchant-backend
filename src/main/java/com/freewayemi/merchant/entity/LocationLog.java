package com.freewayemi.merchant.entity;

import com.amazonaws.services.dynamodbv2.xspec.B;
import lombok.Data;

import java.time.Instant;

@Data
public class LocationLog {
    private String location;
    private Instant from;
    private Instant to;
    private Boolean isLatest;
}
