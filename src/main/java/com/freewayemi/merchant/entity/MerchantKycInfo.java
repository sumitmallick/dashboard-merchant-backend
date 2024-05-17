package com.freewayemi.merchant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class MerchantKycInfo {
    private Boolean isKycDone;
    private String name;
    private String docNumber;
    private String whyNotAadhaarKyc;
    private Instant successDate;


    @JsonCreator
    public MerchantKycInfo(@JsonProperty("isKycDone") Boolean isKycDone,
                           @JsonProperty("name") String name,
                           @JsonProperty("docNumber") String docNumber,
                           @JsonProperty("whyNotAadhaarKyc") String whyNotAadhaarKyc,
                           @JsonProperty("successDate") Instant successDate) {
        this.isKycDone = isKycDone;
        this.whyNotAadhaarKyc = whyNotAadhaarKyc;
        this.name = name;
        this.docNumber = docNumber;
        this.successDate = successDate;
    }
}
