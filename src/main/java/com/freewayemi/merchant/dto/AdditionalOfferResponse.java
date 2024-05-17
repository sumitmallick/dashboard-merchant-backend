package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdditionalOfferResponse {
    private Float offerPrice;
    private String bankName;
    private String cardType;
    private String offerType;
    private String offerSubType;
    private Instant validFrom;
    private Instant validTo;
}

