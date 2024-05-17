package com.freewayemi.merchant.commons.exception;

public class FreewayValidationException extends RuntimeException {
    private String key;
    public FreewayValidationException(String key, String message) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
