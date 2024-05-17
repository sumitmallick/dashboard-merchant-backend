package com.freewayemi.merchant.commons.exception;

public class FreewayException extends RuntimeException {

    private String refType;
    private String refValue;

    public FreewayException(String message) {
        super(message);
    }

    public FreewayException(String message, String refType, String refValue) {
        super(message);
        this.refType = refType;
        this.refValue = refValue;
    }

    public String getRefType() {
        return refType;
    }

    public String getRefValue() {
        return refValue;
    }
}
