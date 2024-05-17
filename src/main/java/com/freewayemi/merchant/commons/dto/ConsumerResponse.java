package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConsumerResponse {

    private final String consumerId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String mobile;
    private final String deviceToken;
    private final String gender;
    private final String dob;
    private final String pincode;


    @JsonCreator
    public ConsumerResponse(@JsonProperty("consumerId") String consumerId, @JsonProperty("firstName") String firstName,
                            @JsonProperty("lastName") String lastName, @JsonProperty("email") String email,
                            @JsonProperty("mobile") String mobile, @JsonProperty("deviceToken") String deviceToken,
                            @JsonProperty("gender") String gender,
                            @JsonProperty("dob") String dob,
                            @JsonProperty("pincode") String pincode) {
        this.consumerId = consumerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.deviceToken = deviceToken;
        this.gender = gender;
        this.dob = dob;
        this.pincode = pincode;
    }
}
