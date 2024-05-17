package com.freewayemi.merchant.type;

import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.type.TransactionCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NtbEligibilityResponseCode {
    NTB_ELIGIBILITY_SUCCESS(276, 201, TransactionCode.SUCCESS.getStatus(), "Eligibility check is completed!"),
    NTB_NOT_ELIGIBLE(96, 202, TransactionCode.SUCCESS.getStatus(), "You are not eligible to avail the loan as credit criteria does not match, please retry again with another finance option."),
    NTB_ELIGIBILITY_PROCESSING(93, 203, TransactionCode.PROCESSING.getStatus(), "We are checking your eligibility with lender, kindly wait till we get a response."),
    REFUND_SUCCESS(70, 204, TransactionCode.SUCCESS.getStatus(), "Refund request has been accepted to process"),
    ;
    private final Integer ntbCode;
    private final Integer code;
    private final String status;
    private final String message;

    NtbEligibilityResponseCode(Integer ntbCode, Integer code, String status, String message) {
        this.ntbCode = ntbCode;
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static NtbEligibilityResponseCode getByNtbCode(Integer ntbCode) {
        return Arrays.stream(NtbEligibilityResponseCode.values())
                .filter(respCode -> respCode.getNtbCode().equals(ntbCode))
                .findFirst()
                .orElseThrow(() -> new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR));
    }
}
