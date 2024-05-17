package com.freewayemi.merchant.commons.juspay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardToken {
    private final String cardToken;
    private final String cardReference;
    private final String cardFingerPrint;

    @JsonCreator
    public CardToken(@JsonProperty("card_token") String cardToken,
                     @JsonProperty("card_reference") String cardReference,
                     @JsonProperty("card_fingerprint") String cardFingerPrint) {
        this.cardToken = cardToken;
        this.cardReference = cardReference;
        this.cardFingerPrint = cardFingerPrint;
    }
}
