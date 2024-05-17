package com.freewayemi.merchant.enums;

public enum BrandMerchantCredentialStatusCode {

    SUCCESS(0, "success", "Success"),
    FAILED(20, "failed", "Something went wrong!"),
    FAILED_101(101, "failed", "Brand don't have fetch-security-credential access"),
    FAILED_102(102, "failed", "No merchants associated with brand");

    private final Integer code;
    private final String status;
    private final String statusMsg;

    BrandMerchantCredentialStatusCode(Integer code, String status, String statusMsg) {
        this.code = code;
        this.status = status;
        this.statusMsg = statusMsg;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public static BrandMerchantCredentialStatusCode getByCode(Integer code) {
        for (BrandMerchantCredentialStatusCode obj : BrandMerchantCredentialStatusCode.values()) {
            if (obj.getCode().equals(code)) {
                return obj;
            }
        }
        return BrandMerchantCredentialStatusCode.FAILED;
    }
}
