package com.freewayemi.merchant.commons.type;

import com.freewayemi.merchant.commons.utils.paymentConstants;

public enum PaymentModeEnum {

    CREDIT(paymentConstants.CREDIT),
    DEBIT(paymentConstants.DEBIT),
    UPI(paymentConstants.UPI),
    CARDLESS(paymentConstants.CARDLESS),
    BNPL(paymentConstants.BNPL),
    NTB(paymentConstants.NTB);

    private String displayMsg;

    PaymentModeEnum(String displayMsg) {
        this.displayMsg = displayMsg;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

}
