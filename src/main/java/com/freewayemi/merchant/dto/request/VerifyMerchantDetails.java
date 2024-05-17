package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyMerchantDetails {
    private final String type;
    private final String pan;
    private final String gst;
    private final String accountNumber;
    private final String ifscCode;

    @JsonCreator
    public VerifyMerchantDetails(@JsonProperty("type") String type,
                                 @JsonProperty("pan") String pan,
                                 @JsonProperty("gst") String gst,
                                 @JsonProperty("accountNumber") String accountNumber,
                                 @JsonProperty("ifscCode") String ifscCode) {
        this.type = type;
        this.pan = pan;
        this.gst = gst;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
    }
}
