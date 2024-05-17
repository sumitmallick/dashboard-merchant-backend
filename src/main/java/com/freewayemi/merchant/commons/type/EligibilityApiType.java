package com.freewayemi.merchant.commons.type;

public enum EligibilityApiType {

    MERCHANT_ELIGIBILITY_API("Merchant Eligibility API"),
    MERCHANT_ELIGIBILITY_API_WITH_OTP("Merchant Eligibility API with OTP"),
    MERCHANT_ELIGIBILITY_API_WITH_CARD_DETAILS("Merchant Eligibility API with Card Details"),
    MERCHANT_SECURE_ELIGIBILITY_API("Merchant Eligibility API"),
    MERCHANT_SECURE_ELIGIBILITY_API_WITH_OTP("Merchant Eligibility API with OTP"),
    MERCHANT_SECURE_ELIGIBILITY_API_WITH_CARD_DETAILS("Merchant Eligibility API with Card Details");

    private final String displayName;

    EligibilityApiType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
