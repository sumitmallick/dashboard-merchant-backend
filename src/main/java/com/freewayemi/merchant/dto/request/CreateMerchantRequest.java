package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateMerchantRequest {
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

    private final String partner;

    private final String referralCode;
    @JsonCreator
    public CreateMerchantRequest(@JsonProperty("mobile") String mobile,
                                 @JsonProperty("email") String email,
                                 @JsonProperty("firstName") String firstName,
                                 @JsonProperty("lastName") String lastName,
                                 @JsonProperty("referredBy") String referredBy,
                                 @JsonProperty("partner") String partner,
                                 @JsonProperty("referralCode") String referralCode) {
        this.mobile = mobile;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.referredBy = referredBy;
        this.partner = partner;
        this.referralCode = referralCode;
    }
}
