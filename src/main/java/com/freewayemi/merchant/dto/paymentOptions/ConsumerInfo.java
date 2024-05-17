package com.freewayemi.merchant.dto.paymentOptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.Address;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerInfo {

    private String consumerId;
    private String email;
    private String mobile;
    private String preferredEligibility;
    private List<EligibilityResponse> eligibilities;
    private Float splitAmount;
    private String consumerName;
    private Address address;
    private Boolean iframe;
    private String firstName;
    private String middleName;
    private String lastName;
    private String returnUrl;
    private Boolean isMobileChange;
    private String pan;
    private String dob;
    private String annualIncome;
    private String referredBy;
    private String gender;
    private String appVersion;
}
