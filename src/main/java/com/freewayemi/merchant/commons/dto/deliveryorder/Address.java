package com.freewayemi.merchant.commons.dto.deliveryorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

    private final String pincode;
    private final String city;
    private final String line1;
    private final String line2;
    private final String state;
    private final String country;

    @JsonCreator
    public Address(@JsonProperty("pincode") String pincode, @JsonProperty("city") String city,
                   @JsonProperty("line1") String line1, @JsonProperty("line2") String line2,
                   @JsonProperty("state") String state, @JsonProperty("country") String country) {
        this.pincode = pincode;
        this.city = city;
        this.line1 = line1;
        this.line2 = line2;
        this.state = state;
        this.country = country;
    }
}
