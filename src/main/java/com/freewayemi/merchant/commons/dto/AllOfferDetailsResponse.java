package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AllOfferDetailsResponse {
    String brandId;
    String transactionId;
    ArrayList<OfferDetailsSingleCardResponse> offerDetailsSingleCardResponseList;

    @JsonCreator
    public AllOfferDetailsResponse(@JsonProperty("brandId") String brandId,
                                     @JsonProperty("transactionId") String transactionId,
                                     @JsonProperty("offerDetailsSingleCardResponseList") ArrayList<OfferDetailsSingleCardResponse> offerDetailsSingleCardResponseList){
        this.brandId = brandId;
        this.transactionId = transactionId;
        this.offerDetailsSingleCardResponseList = offerDetailsSingleCardResponseList;
    }
}
