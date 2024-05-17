package com.freewayemi.merchant.commons.type;

public enum PayeeType {

    CONSUMER("Consumer"), SALES_AGENT("Sales Agent"), MERCHANT("Merchant");

    private final String displayName;

    PayeeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
