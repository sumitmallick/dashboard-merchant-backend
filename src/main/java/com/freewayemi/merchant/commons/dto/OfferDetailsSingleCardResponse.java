package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OfferDetailsSingleCardResponse {
    String key;
    List<OfferDetailsResponse> offerDetailsResponseList;

    @JsonCreator
    public OfferDetailsSingleCardResponse(@JsonProperty("key") String key,
                                          @JsonProperty("offerDetailsResponseList") List<OfferDetailsResponse> offerDetailsResponseList){
        this.key = key;
        this.offerDetailsResponseList = offerDetailsResponseList;
    }
}
