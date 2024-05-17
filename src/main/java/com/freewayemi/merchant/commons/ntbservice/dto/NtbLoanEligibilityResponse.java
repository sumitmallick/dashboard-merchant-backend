package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanEligibilityResponse {

    private final String loanId;
    private final String eligibleAmount;
    private final String availableCreditLimit;
    private final String userType;
    private final String maxCreditLimit;
    private final String redirectionUrl;
    private final String maxTenure;
    private final String minTenure;
    private final String roi;
    private final String processingFeeRate;
    private final String maxProcessingFee;
    private final String error;
    private final String code;
    private final String message;
    private final String loanStatus;
    private final Instant expiry;
    private String state;
    private String city;
    private String pincode;
    private final Float maxTxnAmount;
    private final Float minTxnAmount;

    @JsonCreator
    public NtbLoanEligibilityResponse(@JsonProperty("loanId") String loanId,
                                      @JsonProperty("eligibleAmount") String eligibleAmount,
                                      @JsonProperty("availableCreditLimit") String availableCreditLimit,
                                      @JsonProperty("userType") String userType,
                                      @JsonProperty("maxCreditLimit") String maxCreditLimit,
                                      @JsonProperty("redirectionUrl") String redirectionUrl,
                                      @JsonProperty("maxTenure") String maxTenure,
                                      @JsonProperty("minTenure") String minTenure,
                                      @JsonProperty("roi") String roi,
                                      @JsonProperty("pfPercentage") String processingFeeRate,
                                      @JsonProperty("maxPf") String maxProcessingFee,
                                      @JsonProperty("error") String error,
                                      @JsonProperty("code") String code,
                                      @JsonProperty("message") String message,
                                      @JsonProperty("loanStatus") String loanStatus,
                                      @JsonProperty("state") String state,
                                      @JsonProperty("city") String city,
                                      @JsonProperty("pincode") String pincode,
                                      @JsonProperty("expiry") Instant expiry,
                                      @JsonProperty("maxTxnAmount") Float maxTxnAmount,
                                      @JsonProperty("minTxnAmount") Float minTxnAmount) {
        this.loanId = loanId;
        this.eligibleAmount = eligibleAmount;
        this.availableCreditLimit = availableCreditLimit;
        this.userType = userType;
        this.maxCreditLimit = maxCreditLimit;
        this.redirectionUrl = redirectionUrl;
        this.maxTenure = maxTenure;
        this.minTenure = minTenure;
        this.roi = roi;
        this.processingFeeRate = processingFeeRate;
        this.maxProcessingFee = maxProcessingFee;
        this.error = error;
        this.code = code;
        this.message = message;
        this.loanStatus = loanStatus;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
        this.expiry = expiry;
        this.maxTxnAmount = maxTxnAmount;
        this.minTxnAmount = minTxnAmount;
    }
}
