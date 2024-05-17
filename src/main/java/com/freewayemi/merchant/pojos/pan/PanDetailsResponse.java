package com.freewayemi.merchant.pojos.pan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.pojos.gst.GSTData;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PanDetailsResponse {
    @JsonProperty(value = "orderId")
    private String orderId;
    @JsonProperty(value = "panNumber")
    private String panNumber;

    //PanDetails Response parameters
    @JsonProperty(value = "code")
    private Integer code;
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "statusMessage")
    private String statusMessage;
    @JsonProperty(value = "providerReferenceId")
    private String providerReferenceId;
    @JsonProperty(value = "fullName")
    private String fullName;
    @JsonProperty(value = "last4DigitOfAadhaar")
    private String last4DigitOfAadhaar;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "mobile")
    private String mobile;
    @JsonProperty(value = "gender")
    private String gender;
    @JsonProperty(value = "dateOfBirth")
    private String dateOfBirth;
    @JsonProperty(value = "isAadhaarLinked")
    private Boolean isAadhaarLinked;
    @JsonProperty(value = "panCategory")
    private String panCategory;
    @JsonProperty(value = "fullAddress")
    private String fullAddress;
    @JsonProperty(value = "addressLine1")
    private String addressLine1;
    @JsonProperty(value = "addressLine2")
    private String addressLine2;
    @JsonProperty(value = "addressLine3")
    private String addressLine3;
    @JsonProperty(value = "state")
    private String state;
    @JsonProperty(value = "city")
    private String city;
    @JsonProperty(value = "pincode")
    private String pincode;

    @JsonCreator
    public PanDetailsResponse(@JsonProperty(value = "orderId") String orderId,
                              @JsonProperty(value = "panNumber") String panNumber,
                              @JsonProperty(value = "code") Integer code,
                              @JsonProperty(value = "status") String status,
                              @JsonProperty(value = "statusMessage") String statusMessage,
                              @JsonProperty(value = "providerReferenceId") String providerReferenceId,
                              @JsonProperty(value = "fullName") String fullName,
                              @JsonProperty(value = "last4DigitOfAadhaar") String last4DigitOfAadhaar,
                              @JsonProperty(value = "email") String email,
                              @JsonProperty(value = "mobile") String mobile,
                              @JsonProperty(value = "gender") String gender,
                              @JsonProperty(value = "dateOfBirth") String dateOfBirth,
                              @JsonProperty(value = "isAadhaarLinked") Boolean isAadhaarLinked,
                              @JsonProperty(value = "panCategory") String panCategory,
                              @JsonProperty(value = "fullAddress") String fullAddress,
                              @JsonProperty(value = "addressLine1") String addressLine1,
                              @JsonProperty(value = "addressLine2") String addressLine2,
                              @JsonProperty(value = "addressLine3") String addressLine3,
                              @JsonProperty(value = "state") String state,
                              @JsonProperty(value = "city") String city,
                              @JsonProperty(value = "pincode") String pincode) {
        this.orderId = orderId;
        this.panNumber = panNumber;
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.providerReferenceId = providerReferenceId;
        this.fullName = fullName;
        this.last4DigitOfAadhaar = last4DigitOfAadhaar;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.isAadhaarLinked = isAadhaarLinked;
        this.panCategory = panCategory;
        this.fullAddress = fullAddress;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.state = state;
        this.city = city;
        this.pincode = pincode;
    }
}
