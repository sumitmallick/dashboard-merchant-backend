package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.enums.UserType;
import lombok.Data;

import java.util.List;

@Data
public class ConsumerProfileResponse {
    private final String consumerId;
    private final String consumerProfileId;
    private final String mobile;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String pan;
    private final String dob;       // dd/MM/yyyy
    private final String pinCode;
    private final String gender;
    private final UserType userType;
    private final Boolean isEmailVerified;
    private final Boolean isMobileVerified;
    // new fields
    private final List<String> profileStages;
    private final AddressDto currentAddress;
    private final AddressDto permanentAddress;
    private final Boolean currentAddressSameAsPermanent;
    private final EmploymentDetailsDto employmentDetails;
    private final String profileImage;
    private final String maritalStatus;
    private final String fatherName;

    @JsonCreator
    public ConsumerProfileResponse(
            @JsonProperty("consumerId") String consumerId,
            @JsonProperty("consumerProfileId") String consumerProfileId,
            @JsonProperty("mobile") String mobile,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("middleName") String middleName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("pan") String pan,
            @JsonProperty("dob") String dob,
            @JsonProperty("pinCode") String pinCode,
            @JsonProperty("gender") String gender,
            @JsonProperty("userType") UserType userType,
            @JsonProperty("isEmailVerified") Boolean isEmailVerified,
            @JsonProperty("isMobileVerified") Boolean isMobileVerified,
            @JsonProperty("profileStages") List<String> profileStages,
            @JsonProperty("currentAddress") AddressDto currentAddress,
            @JsonProperty("permanentAddress") AddressDto permanentAddress,
            @JsonProperty("currentAddressSameAsPermanent") Boolean currentAddressSameAsPermanent,
            @JsonProperty("employmentDetails") EmploymentDetailsDto employmentDetails,
            @JsonProperty("profileImage") String profileImage,
            @JsonProperty("maritalStatus") String maritalStatus,
            @JsonProperty("fatherName") String fatherName) {
        this.consumerId = consumerId;
        this.consumerProfileId = consumerProfileId;
        this.mobile = mobile;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.pan = pan;
        this.dob = dob;
        this.pinCode = pinCode;
        this.gender = gender;
        this.userType = userType;
        this.isEmailVerified = isEmailVerified;
        this.isMobileVerified = isMobileVerified;
        this.profileStages = profileStages;
        this.currentAddress = currentAddress;
        this.permanentAddress = permanentAddress;
        this.currentAddressSameAsPermanent = currentAddressSameAsPermanent;
        this.employmentDetails = employmentDetails;
        this.profileImage = profileImage;
        this.maritalStatus = maritalStatus;
        this.fatherName = fatherName;
    }
}
