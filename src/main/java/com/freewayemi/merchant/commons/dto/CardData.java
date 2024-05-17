package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Data;

@Data
public class CardData {

    private final String cardNumber;
    private final String expMonth;
    private final String expYear;
    private final String cvv;

    @JsonCreator
    public CardData(@JsonProperty("cardNumber") String cardNumber, @JsonProperty("expMonth") String expMonth,
                    @JsonProperty("expYear") String expYear, @JsonProperty("cvv") String cvv) {
        this.cardNumber = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "cardNumber: " + Util.getMaskCardNumber(cardNumber) + ", expMonth: " + expMonth + ", expYear: " + expYear
                + ", cvv: " + Util.truncateString(cvv);
    }

}
