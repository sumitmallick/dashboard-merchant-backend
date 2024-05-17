package com.freewayemi.merchant.commons.type;

public enum SubventionType {

    DISCOUNT("DISCOUNT"),
    CASHBACK("CASHBACK");

    SubventionType(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

}
