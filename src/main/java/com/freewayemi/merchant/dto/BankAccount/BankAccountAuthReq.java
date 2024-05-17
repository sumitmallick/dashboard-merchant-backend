package com.freewayemi.merchant.dto.BankAccount;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountAuthReq {
    private final String paymentRefId;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "invalid ifsc")
    private final String ifsc;

    @NotEmpty
    private final String accountNumber;

    private final Source source;
    private final String provider;

    public BankAccountAuthReq(String paymentRefId, String ifsc, String accountNumber, Source source, String provider) {
        this.paymentRefId = paymentRefId;
        this.ifsc = ifsc;
        this.accountNumber = accountNumber;
        this.source = source;
        this.provider = provider;
    }


    public String getProvider() {
        return provider;
    }
}
