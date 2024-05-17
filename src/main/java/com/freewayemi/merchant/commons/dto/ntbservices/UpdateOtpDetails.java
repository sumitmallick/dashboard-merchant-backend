package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateOtpDetails extends UpdateLoanRequestDto{

    private String otp;
}
