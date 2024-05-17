package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProviderConfig {
    private String paymentProvider;
    private BandInfo bandInfo;

    @JsonCreator
    public ProviderConfig(@JsonProperty("paymentProvider") String paymentProvider,
                          @JsonProperty("bandInfo") BandInfo bandInfo) {
        this.paymentProvider = paymentProvider;
        this.bandInfo = bandInfo;
    }
}
