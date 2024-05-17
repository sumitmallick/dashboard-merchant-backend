package com.freewayemi.merchant.commons.dto.ntb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.Address;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditScoreRequest {
    private String orderId;
    private String consumerId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private Address address;
    private String pan;
    private String source;
    private String sourceId;
    private String userConsentId;
    private String bureau;
    private Boolean refresh;
    private String dob;
    private String age;
    private String drivingLicense;
    private String authenticationValue;


    @JsonCreator
    public CreditScoreRequest(@JsonProperty("orderId") String orderId,
                              @JsonProperty("consumerId") String consumerId,
                              @JsonProperty("firstName") String firstName,
                              @JsonProperty("lastName") String lastName,
                              @JsonProperty("mobile") String mobile,
                              @JsonProperty("email") String email,
                              @JsonProperty("address") Address address,
                              @JsonProperty("pan") String pan,
                              @JsonProperty("source") String source,
                              @JsonProperty("sourceId") String sourceId,
                              @JsonProperty("userConsentId") String userConsentId,
                              @JsonProperty("bureau") String bureau,
                              @JsonProperty("refresh") Boolean refresh,
                              @JsonProperty("dob") String dob,
                              @JsonProperty("age") String age,
                              @JsonProperty("drivingLicense") String drivingLicense
    ) {
        this.orderId = orderId;
        this.consumerId = consumerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.pan = pan;
        this.source = source;
        this.sourceId = sourceId;
        this.userConsentId = userConsentId;
        this.bureau = bureau;
        this.refresh = refresh;
        this.dob = dob;
        this.age = age;
        this.drivingLicense = drivingLicense;
    }
}
