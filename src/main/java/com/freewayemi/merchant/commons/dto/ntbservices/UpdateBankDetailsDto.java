package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.commons.dto.ntbservices.validators.ValidationErrorMessages;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBankDetailsDto extends UpdateLoanRequestDto {

    @NotBlank(message = ValidationErrorMessages.UpdateProspectMessage.ACC_IFSC)
    private String bankIfscCode;

    @NotBlank(message = ValidationErrorMessages.UpdateProspectMessage.ACC_NO)
    private String bankAccNumber;

    private String mobileNumber;

    @NotBlank(message = ValidationErrorMessages.UpdateProspectMessage.ACC_NAME)
    private String nameOnAccount;

    private String bankName;

    private String accountType;

    private String prospectId;
    private String loanReferenceId;
    private String applicationNumber;
    private String branchName;
    private String branchCity;
    private String branchState;
}
