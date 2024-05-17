package com.freewayemi.merchant.dto.BankAccount;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.type.Source;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankDetailsRequest
{
    private final String paymentRefId;

    @Pattern(regexp="^[A-Z]{4}0[A-Z0-9]{6}$",message = "invalid ifsc")
    private final String ifsc;
    private final Source source;
    private final String provider;

    public BankDetailsRequest(String paymentRefId, String ifsc, Source source, String provider) {
        this.paymentRefId = paymentRefId;
        this.ifsc = ifsc;
        this.source = source;
        this.provider = provider;
    }

}
