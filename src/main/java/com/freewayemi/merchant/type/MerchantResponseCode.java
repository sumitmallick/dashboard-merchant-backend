package com.freewayemi.merchant.type;

public enum MerchantResponseCode {
    VALID(0, "Success", "Valid model and serial number"),
    FAIL_400(400, "FAILED", "Consumer profile already exists"),
    FAIL_401(401, "FAILED", "Entered PAN is invalid for Consumer profile"),
    FAIL_402(402, "FAILED", "The PAN entered is linked to a different mobile number. Please retry with your own PAN."),
    FAIL_500(500, "Something went wrong!", "Something went wrong!");

    private Integer code;
    private String response;
    private String statusMsg;

    MerchantResponseCode(Integer code, String response, String statusMsg) {
        this.code = code;
        this.response = response;
        this.statusMsg = statusMsg;
    }

    public Integer getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public static MerchantResponseCode getByCode(String message) {
        for (MerchantResponseCode responseCode : MerchantResponseCode.values()) {
            if (responseCode.getStatusMsg().equals(message)) {
                return responseCode;
            }
        }
        return MerchantResponseCode.FAIL_500;
    }
}
