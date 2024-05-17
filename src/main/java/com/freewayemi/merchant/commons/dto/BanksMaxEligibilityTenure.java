package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BanksMaxEligibilityTenure {

    private final String bankCode;
    private final Integer maxEligibleTenure;

    @JsonCreator
    public BanksMaxEligibilityTenure(@JsonProperty("bankCode") String bankCode,
                                     @JsonProperty("maxEligibleTenure") Integer maxEligibleTenure) {
        this.bankCode = bankCode;
        this.maxEligibleTenure = maxEligibleTenure;
    }

}
