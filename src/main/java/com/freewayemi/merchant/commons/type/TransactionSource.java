package com.freewayemi.merchant.commons.type;

import org.springframework.util.StringUtils;

public enum TransactionSource {
    merchantApp("merchantApp"),
    twl("twl"),
    userScannedQr("userScannedQr"),
    merchantPg("merchantPg"),
    paymentLink("paymentLink"),
    other("other"),
    seamless("seamless"),
    storeLink("storeLink"),
    secureApi("secureApi"),
    merchantEligibilityWithCard("merchantEligibilityWithCard"),
    giftVouchers("giftVouchers"),
    secureSeamplessApi("secureSeamplessApi");

    private final String displayName;

    TransactionSource(String displayName) {
        this.displayName = displayName;
    }

    public static TransactionSource getByDisplayName(String displayName) {
        if (!StringUtils.isEmpty(displayName)) {
            for (TransactionSource ts : TransactionSource.values()) {
                if (ts.name().equalsIgnoreCase(displayName)) {
                    return ts;
                }
            }
        }
        return null;
    }
}
