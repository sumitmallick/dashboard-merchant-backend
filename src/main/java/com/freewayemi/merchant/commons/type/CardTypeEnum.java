package com.freewayemi.merchant.commons.type;

import com.freewayemi.merchant.commons.utils.paymentConstants;

public enum CardTypeEnum {

    CREDIT(paymentConstants.CREDIT),
    DEBIT(paymentConstants.DEBIT),
    CARDLESS(paymentConstants.CARDLESS),
    BNPL(paymentConstants.BNPL),
    UPI(paymentConstants.UPI),
    NTB(paymentConstants.NTB);

    private final String cardType;

    CardTypeEnum(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return cardType;
    }

    public static CardTypeEnum getCardTypeEnum(String cardType) {
        for (CardTypeEnum cardTypeEnum : CardTypeEnum.values()) {
            if (cardTypeEnum.getCardType().equals(cardType)) {
                return cardTypeEnum;
            }
        }
        return null;
    }
}
