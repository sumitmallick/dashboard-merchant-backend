package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OfferDetails {
    private String offerId;
    private String type;
    private Float offerAmount;
    private Float bankShareAmt;
    private Float brandShareAmt;
    private Float offerRate;
    private OfferResponse offerConstruct;

    @JsonCreator
    public OfferDetails(@JsonProperty("offerId") String offerId,
                        @JsonProperty("offerAmount") Float offerAmount,
                        @JsonProperty("bankShareAmt") Float bankShareAmt,
                        @JsonProperty("brandShareAmt") Float brandShareAmt,
                        @JsonProperty("offerConstruct") OfferResponse offerConstruct) {
        this.offerId = offerId;
        this.offerAmount = offerAmount;
        this.bankShareAmt = bankShareAmt;
        this.brandShareAmt = brandShareAmt;
        this.offerConstruct = offerConstruct;
        this.offerRate = 0.0f;
    }
}
