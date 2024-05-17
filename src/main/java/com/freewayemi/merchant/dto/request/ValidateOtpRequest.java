package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ValidateOtpRequest {

    private final String otp;
    private final String paymentRefNo;

    @JsonCreator
    public ValidateOtpRequest(@JsonProperty("otp") String otp, @JsonProperty("paymentRefNo") String paymentRefNo) {
        this.otp = otp;
        this.paymentRefNo = paymentRefNo;
    }

}
