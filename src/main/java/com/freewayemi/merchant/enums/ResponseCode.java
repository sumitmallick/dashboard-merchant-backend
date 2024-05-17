package com.freewayemi.merchant.enums;

import com.freewayemi.merchant.entity.MerchantUser;

public enum ResponseCode {
    MERCHANT_EXISTS(0, Status.SUCCESS, "The merchant is already onboarded with payment and can login on the payment Business App."),
    MERCHANT_EXISTS_WITH_DIFFERENT_GST(20, Status.FAILED, "Entered Mobile Number is already associated with a different GST"),
    LEAD_CREATED(0, Status.SUCCESS, "The new lead has been created. The payment Business App Download Link has been sent to the merchant, please ask the merchant to download the app and complete the onboarding process."),
    ONBOARDING_GST_VERIFIED(0, Status.SUCCESS, "Merchant's GST data verified."),
    LEAD_ALREADY_CREATED(20, Status.FAILED, "The lead is already Exists. The payment Business App Download Link has been sent to the merchant, please ask the merchant to download the app and complete the onboarding process."),
    GST_VALIDAION_FAILED(20, Status.FAILED, "GST validation Failed"),
    MERCHANT_ONBOARDING_FAILED(20, Status.FAILED, "Merchant already onboarded");
    public Integer getCode() {
        return code;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String setStatusMsg(String statusMsg, MerchantUser merchantUser) {
        this.statusMsg = statusMsg;
        return statusMsg;
    }

    private final Integer code;
    private final Status status;
    private String statusMsg;

    ResponseCode(int code, Status status, String statusMsg) {
        this.code = code;
        this.status = status;
        this.statusMsg = statusMsg;
    }
}
