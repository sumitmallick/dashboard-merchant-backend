package com.freewayemi.merchant.commons.type;

public enum CheckoutVersion {
    V1("v1"),
    V2("v2"),
    EDUV1("EDUV1"),
    DEFAULT("v2");

    private String version;

    CheckoutVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}

