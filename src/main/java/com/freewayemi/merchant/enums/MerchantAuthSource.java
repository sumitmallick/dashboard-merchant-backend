package com.freewayemi.merchant.enums;

public enum MerchantAuthSource {
    MOBILE("Mobile"), EXTERNAL("External"), INTERNAL("Internal"),
    MERCHANT("Merchant");
    private final String merchantAuthSource;

    MerchantAuthSource(String consumerAuthSource) {
        this.merchantAuthSource = consumerAuthSource;
    }

    public String getConsumerAuthSource() {
        return merchantAuthSource;
    }
}
