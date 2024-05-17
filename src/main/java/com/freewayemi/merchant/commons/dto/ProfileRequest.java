package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProfileRequest {
    private final Address address;
    private final List<String> preferences;
    private final String dob;
    private final String employmentStatus;
    private final String monthlyIncome;
    private final String gender;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final List<ConsumerAccount> accounts;
    private final String pincode;
    private final String email;
    private final String source;
    private final String pan;
    private final String title;
    private final String referredBy;

    @JsonCreator
    public ProfileRequest(@JsonProperty("preferences") List<String> preferences,
                          @JsonProperty("address") Address address,
                          @JsonProperty("dob") String dob,
                          @JsonProperty("employmentStatus") String employmentStatus,
                          @JsonProperty("monthlyIncome") String monthlyIncome,
                          @JsonProperty("gender") String gender,
                          @JsonProperty("firstName") String firstName,
                          @JsonProperty("middleName") String middleName,
                          @JsonProperty("lastName") String lastName,
                          @JsonProperty("accounts") List<ConsumerAccount> accounts,
                          @JsonProperty("pincode") String pincode,
                          @JsonProperty("email") String email,
                          @JsonProperty("source") String source,
                          @JsonProperty("pan") String pan,
                          @JsonProperty("title") String title,
                          @JsonProperty("referredBy") String referredBy) {
        this.address = address;
        this.preferences = preferences;
        this.dob = dob;
        this.employmentStatus = employmentStatus;
        this.monthlyIncome = monthlyIncome;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = accounts;
        this.pincode = pincode;
        this.email=email;
        this.source = source;
        this.pan=pan;
        this.title=title;
        this.middleName=middleName;
        this.referredBy=referredBy;
    }
}
