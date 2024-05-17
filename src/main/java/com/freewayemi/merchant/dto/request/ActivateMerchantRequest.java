package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActivateMerchantRequest {
    private final String status;
    private final List<String> offers;

    @JsonCreator
    public ActivateMerchantRequest(@JsonProperty("status") String status,
                                   @JsonProperty("offers") List<String> offers) {
        this.status = status;
        this.offers = offers;
    }
}
