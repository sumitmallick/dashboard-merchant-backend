package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAddressRequestDto extends UpdateLoanRequestDto {

    private String currentAddress1;

    private String currentAddress2;

    private String currentAddress3;

    private String currentPincode;

    private String currentState;

    private String currentCity;

    private boolean currentAddressSameAsPermanent;

    private String permanentAddress1;

    private String permanentAddress2;

    private String permanentAddress3;

    private String permanentPincode;

    private String permanentState;

    private String permanentCity;

    private ResidentType residentType;
}
