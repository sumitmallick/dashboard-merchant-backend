package com.freewayemi.merchant.commons.exception;

import com.freewayemi.merchant.commons.type.TransactionCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FreewayCustomException extends RuntimeException {

    private int code;
    private String message;

    public FreewayCustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public FreewayCustomException(TransactionCode transactionCode) {
        this.code = transactionCode.getCode();
        this.message = transactionCode.getStatusMsg();
    }

}
