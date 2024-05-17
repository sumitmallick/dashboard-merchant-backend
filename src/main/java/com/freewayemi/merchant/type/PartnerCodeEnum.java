package com.freewayemi.merchant.type;
import com.freewayemi.merchant.commons.utils.paymentConstants;

public enum PartnerCodeEnum {
    payment(paymentConstants.DEFAULT_SCHEME_PARTNER),
    VIVO_HDFC(paymentConstants.VIVO_HDFC_SCHEME_PARTNER);
    private final String partnerCode;
    PartnerCodeEnum(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    public String getPartnerCode() {
        return partnerCode;
    }
}