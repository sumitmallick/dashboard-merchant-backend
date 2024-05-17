package com.freewayemi.merchant.enums;

public enum EligibilityResponseCode {

    SUCCESS(0, "success"),
    SUCCESS_WITH_OTP(0, "OTP has been sent successfully"),
    FAILED_10(10, "Something went wrong!!"),
    FAILED_11(11, "You have entered incorrect OTP"),
    FAILED_12(12, "Sorry, your OTP is expired"),
    FAILED_13(13, "Invalid payment reference number.");

    private int code;
    private String message;

    EligibilityResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
