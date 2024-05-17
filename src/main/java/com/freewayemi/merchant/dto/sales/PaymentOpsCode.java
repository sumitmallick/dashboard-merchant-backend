package com.freewayemi.merchant.dto.sales;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum PaymentOpsCode {

    SSEPOPS000(0, PaymentOpsStatus.SUCCESS, "Request processed successfully"),

    SSEPOPS001(1, PaymentOpsStatus.FAILED, "Request failed. Please try again.");

    private final Integer code;
    private final String status;
    private final String statusMsg;

    PaymentOpsCode(Integer code, PaymentOpsStatus status, String statusMsg) {
        this.code = code;
        this.status = status.name();
        this.statusMsg = statusMsg;
        TransactionCodeMapping.TRANSACTION_CODE_MAP.put(code, this);
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

    private static class TransactionCodeMapping {
        static Map<Integer, PaymentOpsCode> TRANSACTION_CODE_MAP = new HashMap<>();
    }

    public static PaymentOpsCode getByCode(Integer code) {
        PaymentOpsCode paymentOpsCode = TransactionCodeMapping.TRANSACTION_CODE_MAP.get(code);
        if (Objects.nonNull(paymentOpsCode)) {
            return paymentOpsCode;
        }
        return PaymentOpsCode.SSEPOPS001;
    }

}

