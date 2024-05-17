package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BandInfo {
    private String band;
    private Float minAmount;
    private Float maxAmount;

    @JsonCreator
    public BandInfo(@JsonProperty("band") String band,
                    @JsonProperty("minAmount") Float minAmount,
                    @JsonProperty("maxAmount") Float maxAmount) {
        this.band = band;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
