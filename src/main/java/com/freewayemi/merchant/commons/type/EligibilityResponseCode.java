package com.freewayemi.merchant.commons.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum EligibilityResponseCode {

    INTERNAL_SERVER_ERROR(500, "M500", "Something went wrong! Please try later"),

    MAXIMUM_AMOUNT_CHECK_FAILED(19, "M462", "Transaction amount is more than bank's maximum eligiblility amount"),
    MINIMUM_AMOUNT_CHECK_FAILED(18, "M461", "Transaction amount is less than bank's minimum eligiblility amount"),
    CUSTOMER_NOT_ELIGIBLE(21, "M463", "The customer is not eligible");

    private final Integer code;
    private final String merchantCode;
    private final String message;

    EligibilityResponseCode(Integer code, String merchantCode, String message) {
        this.code = code;
        this.merchantCode = merchantCode;
        this.message = message;
        CodeMapping.ELIGIBILITY_CODE_MAP.put(code, this);
    }

    public Integer getCode() {
        return code;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }

    public static String findMerchantCodeByCode(Integer code) {
        EligibilityResponseCode eligibilityResponseCode = CodeMapping.ELIGIBILITY_CODE_MAP.get(code);
        if (Objects.nonNull(eligibilityResponseCode)) {
            return eligibilityResponseCode.getMerchantCode();
        }
        return EligibilityResponseCode.INTERNAL_SERVER_ERROR.getMerchantCode();
    }

    private static class CodeMapping {
        static Map<Integer, EligibilityResponseCode> ELIGIBILITY_CODE_MAP = new HashMap<>();
    }
}
