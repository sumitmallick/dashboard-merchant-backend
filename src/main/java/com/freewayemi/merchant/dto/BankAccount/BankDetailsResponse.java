package com.freewayemi.merchant.dto.BankAccount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class BankDetailsResponse{
    private String code;
    private String status;
    private String statusMessage;
    private final String city;
    private final String address;
    private final String state;
    private final String upi;
    private final String rtgs;
    private final String neft;
    private final String imps;
    private final String district;
    private final String centre;
    private final String contact;
    private final String branch;
    private final String swift;
    private final String micr;
    private final String bank;
    private final String bankCode;
    private final String ifsc;
    private String paymentRefId;

    @JsonCreator
    public BankDetailsResponse(@JsonProperty("paymentRefId") String paymentRefId,
                           @JsonProperty("code") String code,
                           @JsonProperty("status") String status,
                           @JsonProperty("statusMessage") String statusMessage,
                           @JsonProperty(value = "city") String city,
                           @JsonProperty(value = "address") String address,
                           @JsonProperty(value = "state") String state,
                           @JsonProperty(value = "upi") String upi,
                           @JsonProperty(value = "rtgs") String rtgs,
                           @JsonProperty(value = "neft") String neft,
                           @JsonProperty(value = "imps") String imps,
                           @JsonProperty(value = "district") String district,
                           @JsonProperty(value = "centre") String centre,
                           @JsonProperty(value = "contact") String contact,
                           @JsonProperty(value = "branch") String branch,
                           @JsonProperty(value = "swift") String swift,
                           @JsonProperty(value = "micr") String micr,
                           @JsonProperty(value = "bank") String bank,
                           @JsonProperty(value = "bankCode") String bankCode,
                           @JsonProperty(value = "ifsc") String ifsc) {
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.city = city;
        this.address = address;
        this.state = state;
        this.upi = upi;
        this.rtgs = rtgs;
        this.neft = neft;
        this.imps = imps;
        this.district = district;
        this.centre = centre;
        this.contact = contact;
        this.branch = branch;
        this.swift = swift;
        this.micr = micr;
        this.bank = bank;
        this.bankCode = bankCode;
        this.ifsc = ifsc;
        this.paymentRefId = paymentRefId;
    }
}
