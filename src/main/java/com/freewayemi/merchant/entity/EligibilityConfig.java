package com.freewayemi.merchant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EligibilityConfig {

    private Boolean checkEligibilityWithOtp;

    @JsonCreator
    public EligibilityConfig(@JsonProperty("checkEligibilityWithOtp") Boolean checkEligibilityWithOtp) {
        this.checkEligibilityWithOtp = checkEligibilityWithOtp;
    }

}
