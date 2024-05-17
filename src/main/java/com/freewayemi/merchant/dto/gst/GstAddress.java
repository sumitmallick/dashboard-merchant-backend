package com.freewayemi.merchant.dto.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GstAddress {

    private String address;
    private String natureOfBusinessAtAddress;
    private String completeAddress;
    private String email;
    private String lastUpdatedDateOfAddress;
    private String mobileNumber;
    @JsonCreator
    public GstAddress(@JsonProperty("email") String email,
                      @JsonProperty("mobileNumber") String mobileNumber,
                      @JsonProperty("natureOfBusinessAtAddress") String natureOfBusinessAtAddress,
                      @JsonProperty("completeAddress") String completeAddress,
                      @JsonProperty("lastUpdatedDateOfAddress") String lastUpdatedDateOfAddress,
                      @JsonProperty("address") String address) {
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.natureOfBusinessAtAddress = natureOfBusinessAtAddress;
        this.completeAddress = completeAddress;
        this.lastUpdatedDateOfAddress = lastUpdatedDateOfAddress;
        this.address =address;
    }
}