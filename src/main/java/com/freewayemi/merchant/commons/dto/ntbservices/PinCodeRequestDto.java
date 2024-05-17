package com.freewayemi.merchant.commons.dto.ntbservices;

import com.freewayemi.merchant.commons.dto.ntbservices.validators.ValidationErrorMessages;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PinCodeRequestDto {

    @NotBlank(message= ValidationErrorMessages.CreateLoanValidationMessage.PINCODE_VALIDATION_MESSAGE)
    public String pinCode;

    @NotBlank(message= ValidationErrorMessages.CreateLoanValidationMessage.PROVIDER_NOT_PRESENT)
    public String provider;
}
