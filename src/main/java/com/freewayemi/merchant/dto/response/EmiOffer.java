package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmiOffer {
    private final String id;
    private final Float rate;
    private final List<Integer> nceTenures;
    private final List<Integer> lceTenures;

    @JsonCreator
    public EmiOffer(@JsonProperty("id") String id,
                    @JsonProperty("rate") Float rate,
                    @JsonProperty("nceTenures") List<Integer> nceTenures,
                    @JsonProperty("lceTenures") List<Integer> lceTenures) {
        this.id = id;
        this.nceTenures = nceTenures;
        this.lceTenures = lceTenures;
        this.rate = rate;
    }
}
