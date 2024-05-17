package com.freewayemi.merchant.enums;

public enum PgTokenRequestType {
    MERCHANT("Merchant"),
    BRAND("Brand");

    private String type;

    PgTokenRequestType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
