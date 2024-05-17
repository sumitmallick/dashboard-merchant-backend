package com.freewayemi.merchant.commons.type;

public enum PayoutProviderEnum {

    MOCK("mock", "Mock"),
    YESBANK("yesBank", "Yes Bank");

    private final String code;
    private final String displayName;

    PayoutProviderEnum(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

}
