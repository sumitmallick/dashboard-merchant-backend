package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
public class ConsumerCreationRequest {
    @NotNull(message = "Please provide mobile.")
    private final String mobile;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String pan;
    private final String dob;
    private final String pinCode;
    private final String gender;
    private final List<String> profileStages;
    private final AddressDto currentAddress;
    private final AddressDto permanentAddress;
    private final Boolean currentAddressSameAsPermanent;
    private final EmploymentDetailsDto employmentDetails;
    private final String maritalStatus;
    private final String fatherName;
    private final Boolean isEmailVerified;
}
