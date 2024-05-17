package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDetailsResp {
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

    @JsonCreator
    public BankDetailsResp(@JsonProperty(value = "CITY") String city,
                           @JsonProperty(value = "ADDRESS") String address,
                           @JsonProperty(value = "STATE") String state,
                           @JsonProperty(value = "UPI") String upi,
                           @JsonProperty(value = "RTGS") String rtgs,
                           @JsonProperty(value = "NEFT") String neft,
                           @JsonProperty(value = "IMPS") String imps,
                           @JsonProperty(value = "DISTRICT") String district,
                           @JsonProperty(value = "CENTRE") String centre,
                           @JsonProperty(value = "CONTACT") String contact,
                           @JsonProperty(value = "BRANCH") String branch,
                           @JsonProperty(value = "SWIFT") String swift,
                           @JsonProperty(value = "MICR") String micr,
                           @JsonProperty(value = "BANK") String bank,
                           @JsonProperty(value = "BANKCODE") String bankCode,
                           @JsonProperty(value = "IFSC") String ifsc) {
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
    }
}
