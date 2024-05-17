package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmiPricingResponse {
    private final List<EmiOffer> offers;
    private final Boolean hidePricingProposal;

    @JsonCreator
    public EmiPricingResponse(@JsonProperty("offers") List<EmiOffer> offers,
                       @JsonProperty("hidePricingProposal") Boolean hidePricingProposal) {
        this.hidePricingProposal = hidePricingProposal;
        this.offers = offers;
    }
}
