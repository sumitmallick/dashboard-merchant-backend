package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.commons.dto.PaymentTransactionRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckNtbEligibilityRequest {
    private final String mobile;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String email;
    private final String pan;
    private final String dob;
    private final String pinCode;
    private final String gender;
    private final AddressDto currentAddress;
    private final AddressDto permanentAddress;
    private final Boolean currentAddressSameAsPermanent;
    private final EmploymentDetailsDto employmentDetails;
    private final String maritalStatus;
    private final String fatherName;
    private final Float amount;
    private String orderId;
    private String productName;
    private String productSkuCode;
    private String returnUrl;
    private String webhookUrl;
    private final Map<String, String> customParams;
    private final List<PaymentTransactionRequest.ProductInfo> products;
    private final Consent consent;
    private final String providerGroup;
    private MobileConsent mobileConsent;
    private EmailConsent emailConsent;
}
