package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.type.OfferType;
import lombok.Data;

import java.time.Instant;

@Data
public class VelocityConfig {

    private Integer velocity;
    private Instant validFrom;
    private Instant validTo;
    private OfferType offerType;
}
