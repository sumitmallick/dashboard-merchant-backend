package com.freewayemi.merchant.pojos.pan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PANData {
    //PanDetails Request parameters
    private String orderId;
    private String panNumber;

    //PanDetails Response parameters
    private Integer code;
    private String status;
    private String statusMessage;

    private String providerReferenceId;
    private String fullName;
    private String last4DigitOfAadhaar;
    private String email;
    private String mobile;
    private String gender;
    private String dateOfBirth;
    private Boolean isAadhaarLinked;
    private String panCategory;

    private String fullAddress;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String state;
    private String city;
    private String pincode;
}
