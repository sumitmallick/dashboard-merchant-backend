package com.freewayemi.merchant.bo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateParentMerchantRequest {
    @NotEmpty(message = "Please provide mobile number.")
    private final String mobile;

    @NotEmpty(message = "Please provide email.")
    @Email(message = "Please provide valid email.")
    private final String email;

    @NotEmpty(message = "Please provide first name.")
    private final String firstName;

    @NotEmpty(message = "Please provide last name.")
    private final String lastName;

    private final String referredBy;

    private final List<String> partners;
    private final List<String> partnerMerchants;

    @JsonCreator
    public CreateParentMerchantRequest(@JsonProperty("mobile") String mobile,
                                @JsonProperty("email") String email,
                                @JsonProperty("firstName") String firstName,
                                @JsonProperty("lastName") String lastName,
                                @JsonProperty("referredBy") String referredBy,
                                @JsonProperty("partners") List<String> partners,
                                @JsonProperty("partners") List<String> partnerMerchants) {
        this.mobile = mobile;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.referredBy = referredBy;
        this.partners = partners;
        this.partnerMerchants = partnerMerchants;
    }
}
