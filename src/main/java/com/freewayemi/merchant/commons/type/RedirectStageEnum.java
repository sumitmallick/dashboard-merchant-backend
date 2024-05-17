package com.freewayemi.merchant.commons.type;

public enum RedirectStageEnum {

    OTP_PAGE("otp-page"), ORDER_SUMMARY("order-summary");

    RedirectStageEnum(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}
