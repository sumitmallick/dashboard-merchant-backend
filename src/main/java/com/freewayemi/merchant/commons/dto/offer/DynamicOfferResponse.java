package com.freewayemi.merchant.commons.dto.offer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@JsonDeserialize(builder = DynamicOfferResponse.DynamicOfferResponseBuilder.class)
@Builder(builderClassName = "DynamicOfferResponseBuilder", toBuilder = true)
@ToString
public class DynamicOfferResponse {
    private final String type;
    private final List<DynamicOffer> dynamicOffers;
    private Float ccBaseRate;
    private Float dcBaseRate;
    private Boolean lowCostEmi;
    private Boolean enableConvenienceFee;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DynamicOfferResponseBuilder {
    }
}
