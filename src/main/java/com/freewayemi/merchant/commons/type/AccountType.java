package com.freewayemi.merchant.commons.type;

public enum AccountType {

    ESCROW("ESCROW"),
    SAVING("SAVING"),
    CURRENT("CURRENT");

    private final String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
