package com.freewayemi.merchant.dto.response;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderSchemeDetail {
    private final String cardInterestId;
    private final Integer tenure;
    private final Integer tenureInDays;
    private final Float pf;
    private final Float bankIrr;
    private final Float brandIrr;
    private final Float actualIrr;
    private final String minTxnVal;
    private final String maxTxnVal;
    // ICICI CL - scheme related details
    private final String providerSchemeDetail1;
    private final String providerSchemeDetail2;
    // merchant bearing discount
    private final Float mbd;
    // dealer bearing discount
    private final Float dbd;
    private final String applicableDays;
    private final String schemeDescription;
    private final String calculationType;
    private final String bankInterestType;
    private final Boolean isActive;

    @JsonCreator
    public ProviderSchemeDetail(@JsonProperty("cardInterestId") String cardInterestId,
                                @JsonProperty("tenure") Integer tenure,
                                @JsonProperty("tenureInDays") Integer tenureInDays, @JsonProperty("pf") Float pf,
                                @JsonProperty("bankIrr") Float bankIrr, @JsonProperty("brandIrr") Float brandIrr,
                                @JsonProperty("actualIrr") Float actualIrr, @JsonProperty("minTxnVal") String minTxnVal,
                                @JsonProperty("maxTxnVal") String maxTxnVal,
                                @JsonProperty("providerSchemeDetail1") String providerSchemeDetail1,
                                @JsonProperty("providerSchemeDetail2") String providerSchemeDetail2,
                                @JsonProperty("mbd") Float mbd, @JsonProperty("dbd") Float dbd,
                                @JsonProperty("applicableDays") String applicableDays,
                                @JsonProperty("schemeDescription") String schemeDescription,
                                @JsonProperty("calculationType") String calculationType,
                                @JsonProperty("bankInterestType") String bankInterestType,
                                @JsonProperty("isActive") Boolean isActive) {
        this.cardInterestId = cardInterestId;
        this.tenure = tenure;
        this.tenureInDays = tenureInDays;
        this.pf = pf;
        this.bankIrr = bankIrr;
        this.brandIrr = brandIrr;
        this.actualIrr = actualIrr;
        this.minTxnVal = minTxnVal;
        this.maxTxnVal = maxTxnVal;
        this.providerSchemeDetail1 = providerSchemeDetail1;
        this.providerSchemeDetail2 = providerSchemeDetail2;
        this.mbd = mbd;
        this.dbd = dbd;
        this.applicableDays = applicableDays;
        this.schemeDescription = schemeDescription;
        this.calculationType = calculationType;
        this.bankInterestType = bankInterestType;
        this.isActive = isActive;
    }
}