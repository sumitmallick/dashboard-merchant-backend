package com.freewayemi.merchant.commons.dto.ntbservices;

import com.freewayemi.merchant.commons.dto.ntbservices.validators.Amount;
import com.freewayemi.merchant.commons.dto.ntbservices.validators.ValidationErrorMessages;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateEMIDetailsDto extends UpdateLoanRequestDto{

    @Amount(message = ValidationErrorMessages.UpdateProspectMessage.AMOUNT)
    private String amount;

    @Amount(message = ValidationErrorMessages.UpdateProspectMessage.EMI_AMOUNT)
    private String emiAmount;

    @Min(value = 3,message = ValidationErrorMessages.UpdateProspectMessage.TENURE)
    private String tenure;

    private String downpaymentAmount; //todo wire to transaction after subvention deduction

    private String paymentDiscount; //todo wire to transaction

    @Amount(message = ValidationErrorMessages.UpdateProspectMessage.PRINCIPAL)
    private String principal;

    @Amount(message = ValidationErrorMessages.UpdateProspectMessage.ROI)
    private String roi;
}
