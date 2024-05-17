package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderEligibilityData {
    private String providerName;
    private Boolean isEligible;
    private String paymentLink;
    private Boolean bankStatementRequired;

    public ProviderEligibilityData(@JsonProperty("providerName") String providerName,
                                   @JsonProperty("isEligible") Boolean isEligible,
                                   @JsonProperty("bankStatementRequired") Boolean bankStatementRequired) {
        this.providerName = providerName;
        this.isEligible = isEligible;
        this.bankStatementRequired = bankStatementRequired;
    }

}
