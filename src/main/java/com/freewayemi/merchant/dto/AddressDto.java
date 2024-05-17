package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.ntbservices.ResidentType;
import lombok.Data;

@Data
public class AddressDto {
    private final String pincode;
    private final String city;
    private final String line1;
    private final String line2;
    private final String line3;
    private final String state;
    private final String country;
    private final String residentType;

    @JsonCreator
    public AddressDto(@JsonProperty("pincode") String pincode, @JsonProperty("city") String city,
                      @JsonProperty("line1") String line1, @JsonProperty("line2") String line2,
                      @JsonProperty("line3") String line3, @JsonProperty("state") String state,
                      @JsonProperty("country") String country,
                      @JsonProperty("residentType") String residentType) {
        this.pincode = pincode;
        this.city = city;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.state = state;
        this.country = country;
        this.residentType = residentType;
    }
}
