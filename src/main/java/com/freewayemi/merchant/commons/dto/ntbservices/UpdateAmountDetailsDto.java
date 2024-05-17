package com.freewayemi.merchant.commons.dto.ntbservices;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateAmountDetailsDto extends UpdateLoanRequestDto{

    private String requestedAmount;

}
