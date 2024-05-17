package com.freewayemi.merchant.pojos.pan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    @JsonProperty(value = "line_1")
    private final String line1;

    @JsonProperty(value = "line_2")
    private final String line2;

    @JsonProperty(value = "street_name")
    private final String streetName;

    @JsonProperty(value = "zip")
    private final String zip;

    @JsonProperty(value = "city")
    private final String city;

    @JsonProperty(value = "state")
    private final String state;

    @JsonProperty(value = "country")
    private final String country;

    @JsonProperty(value = "full")
    private final String fullAddress;

    @JsonCreator
    public Address(@JsonProperty(value = "line_1") String line1,
                   @JsonProperty(value = "line_2") String line2,
                   @JsonProperty(value = "street_name") String streetName,
                   @JsonProperty(value = "zip") String zip,
                   @JsonProperty(value = "city") String city,
                   @JsonProperty(value = "state") String state,
                   @JsonProperty(value = "country") String country,
                   @JsonProperty(value = "full") String fullAddress) {
        this.line1 = line1;
        this.line2 = line2;
        this.streetName = streetName;
        this.zip = zip;
        this.city = city;
        this.state = state;
        this.country = country;
        this.fullAddress = fullAddress;
    }
}
