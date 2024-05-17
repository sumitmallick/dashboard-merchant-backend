package com.freewayemi.merchant.commons.exception;

import com.freewayemi.merchant.commons.type.MerchantResponseCode;

public class MerchantException extends RuntimeException {

    private String code;
    private Integer statusCode;

    public MerchantException(String message) {
        super(message);
    }

    public MerchantException(Integer statusCode, String code, String message) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
    }

    public MerchantException(MerchantResponseCode responseCode) {
        this(responseCode.getHttpStatusCode(), responseCode.getCode(), responseCode.getMessage());
    }

    public MerchantException(Integer statusCode, MerchantResponseCode responseCode) {
        this(statusCode, responseCode.getCode(), responseCode.getMessage());
    }

    public MerchantException(MerchantResponseCode responseCode, Object... args) {
        this(responseCode.getHttpStatusCode(), responseCode.getCode(), responseCode.getFormattedMessage(args));
    }

    public MerchantException(Integer statusCode, MerchantResponseCode responseCode, Object... args) {
        this(statusCode, responseCode.getCode(), responseCode.getFormattedMessage(args));
    }

    public String getCode() {
        return code;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
