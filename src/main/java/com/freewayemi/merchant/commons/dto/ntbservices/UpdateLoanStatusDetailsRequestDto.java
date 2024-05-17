package com.freewayemi.merchant.commons.dto.ntbservices;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateLoanStatusDetailsRequestDto extends UpdateLoanRequestDto {

    private String status;

    private String leadId;

    private String providerReferenceId;
}
