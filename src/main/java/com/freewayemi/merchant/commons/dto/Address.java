package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private String pincode;
    private String city;
    private String line1;
    private String line2;
    private String state;
    private String country;
    private List<Double> coordinates;
    private List<Double> reverseCoordinates;
    private List<Double> userCoordinates;
    private List<Double> reverseUserCoordinates;
    private Boolean isGstAddress;
    private String source;
    private GoogleInfo googleInfo;
    private String displayString;

    @JsonCreator
    public Address(@JsonProperty("pincode") String pincode, @JsonProperty("city") String city,
                   @JsonProperty("line1") String line1, @JsonProperty("line2") String line2,
                   @JsonProperty("state") String state, @JsonProperty("country") String country,
                   @JsonProperty("coordinates") List<Double> coordinates,
                   @JsonProperty("source") String source,
                   @JsonProperty("isGstAddress") Boolean isGstAddress,
                   @JsonProperty("googleInfo") GoogleInfo googleInfo) {
        this.pincode = pincode;
        this.city = city;
        this.line1 = line1;
        this.line2 = line2;
        this.state = state;
        this.country = country;
        this.coordinates = coordinates;
        this.isGstAddress = isGstAddress;
        this.source = source;
        this.googleInfo = googleInfo;
    }

    @Override
    public String toString() {
        return String.format(Optional.ofNullable(line1).orElse("") + "," + Optional.ofNullable(line2).orElse("") + ","
                + Optional.ofNullable(city).orElse("") + "," + Optional.ofNullable(pincode).orElse("") + ","
                + Optional.ofNullable(state).orElse("") + "," + Optional.ofNullable(country).orElse(""));
    }

    public String getDisplayString() {
        return String.format(Optional.ofNullable(line1).orElse("") + ", " + Optional.ofNullable(line2).orElse("") + ", "
                + Optional.ofNullable(city).orElse("") + "-" + Optional.ofNullable(pincode).orElse(""));
    }
}
