package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConvFeeRate {
    private final Integer tenure;
    private final Float rate;
    private final String cardType;
    private final String bankCode;
    private final String type; //flat, percentage
    private Integer score;

    @JsonCreator
    public ConvFeeRate(@JsonProperty("tenure") Integer tenure,
                       @JsonProperty("rate") Float rate,
                       @JsonProperty("cardType") String cardType,
                       @JsonProperty("bankCode") String bankCode,
                       @JsonProperty("type") String type) {
        this.tenure = tenure;
        this.rate = rate;
        this.cardType = cardType;
        this.bankCode = bankCode;
        this.type = type;
    }

    public ConvFeeRate setScore(Integer score) {
        this.score = score;
        return this;
    }

}
