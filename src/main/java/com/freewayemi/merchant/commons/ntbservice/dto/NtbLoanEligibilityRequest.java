package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanEligibilityRequest {
    private final String transactionId;
    private final String mobileNumber;
    private final String provider;
    private final Float amount;
    private final String pan;
    private final String latitude;
    private final String longitude;
    private final String ip;

    @JsonCreator
    public NtbLoanEligibilityRequest(String transactionId, String mobileNumber, String provider, Float amount, String pan,String latitude,String longitude,String ip) {
        this.transactionId = transactionId;
        this.mobileNumber = mobileNumber;
        this.provider = provider;
        this.amount = amount;
        this.pan = pan;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
    }
}
