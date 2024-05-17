package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class AgreementDetails {
    private String merchantServiceAgreementUrl;
    private String merchantCommercialsAgreementUrl;
    private String merchantNtbAgreementUrl;
    private String otp;
    private String mobileNumber;
    private String email;
    private String latitude;
    private String longitude;
    private String stage;
}
