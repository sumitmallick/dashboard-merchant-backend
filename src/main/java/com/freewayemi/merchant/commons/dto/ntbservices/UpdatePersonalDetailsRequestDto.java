package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePersonalDetailsRequestDto extends UpdateLoanRequestDto {

    //@NotBlank(message= ValidationErrorMessages.UpdateProspectMessage.FATHER_NAME)
    private String fatherName;

    //@NotBlank(message = ValidationErrorMessages.UpdateProspectMessage.MOTHER_NAME)
    private String motherName;

    private String spouseName;

    //@NotBlank(message = ValidationErrorMessages.UpdateProspectMessage.GENDER)
    private String gender;

    private String pincode;

    private Profession profession;

    private MaritalStatus maritalStatus;

    private String monthlyNetSalary;

    private ResidentType residentType;

    private String education;
}
