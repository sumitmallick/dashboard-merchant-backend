package com.freewayemi.merchant.enums;

public enum ConsumerAppBrandOfferType {
    STORE_OFFER("storeOffer"),
    BANK_OFFER("bankOffer");

    private String offerType;

    ConsumerAppBrandOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getOfferType() {
        return offerType;
    }
}
