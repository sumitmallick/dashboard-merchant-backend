package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.freewayemi.merchant.commons.ntbservice.dto.LenderResponseDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "updateType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateAddressRequestDto.class, name = "address_details"),
        @JsonSubTypes.Type(value = UpdateAmountDetailsDto.class, name = "amount_details"),
        @JsonSubTypes.Type(value = UpdateEMIDetailsDto.class, name = "emi_details"),
        @JsonSubTypes.Type(value = UpdateBankDetailsDto.class, name = "bank_details"),
        @JsonSubTypes.Type(value = UpdatePersonalDetailsRequestDto.class, name = "personal_details"),
        @JsonSubTypes.Type(value = UpdateLoanStatusDetailsRequestDto.class, name = "status_details"),
        @JsonSubTypes.Type(value = UpdateOtpDetails.class,name="otp_details")
})
public class UpdateLoanRequestDto {

    private String prospectId;

    private String updateType;

    @JsonProperty(value = "transactionId", required = false)
    private String transactionId;

    @JsonProperty(value = "lenderResponseDetails", required = false)
    private LenderResponseDetails lenderResponseDetails;
}
