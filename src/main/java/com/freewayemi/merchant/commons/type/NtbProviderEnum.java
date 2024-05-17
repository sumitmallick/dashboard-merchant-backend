package com.freewayemi.merchant.commons.type;

import com.freewayemi.merchant.commons.exception.FreewayException;

public enum NtbProviderEnum {
    KKBK, IIFL, HDFC;

    public NtbProviderEnum getNtbProviderWithPaymentProvider(PaymentProviderEnum paymentProviderEnum) {
        switch (paymentProviderEnum.name()) {
            case "hdfcntb":
                return HDFC;
            case "kotakntb":
                return KKBK;
            case "iifl":
                return IIFL;
            default:
                throw new FreewayException("Ntb provider not found!");
        }
    }

    public static PaymentProviderEnum getPaymentProviderWithNtbProvider(NtbProviderEnum ntbProviderEnum) {
        switch (ntbProviderEnum.name()) {
            case "KKBK":
                return PaymentProviderEnum.kotakntb;
            case "IIFL":
                return PaymentProviderEnum.iifl;
            case "HDFC":
                return PaymentProviderEnum.hdfcntb;
            default:
                return PaymentProviderEnum.none;
        }
    }
}
