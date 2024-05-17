package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = PaymentLinkDetails.PaymentLinkDetailsBuilder.class)
@Builder(builderClassName = "PaymentLinkDetailsBuilder", toBuilder = true)
public class PaymentLinkDetails {

    private final String merchantId;
    private final String merchantLogo;
    private final String orderId;
    private final String shopName;
    private final String customerName;
    private final String firstName;
    private final String lastName;
    private final String mobile;
    private final String email;
    private final String uuid;
    private final Float amount;
    private final String skipInfoForm;
    private final String skipIntro;
    private final String skipNoCostEmiText;
    private final Boolean isSeamlessPaymentLink;
    private final String transactionId;
    private final String dob;
    private final String gender;
    private final Address address;


    @JsonPOJOBuilder(withPrefix = "")
    public static class PaymentLinkResponseBuilder {
    }

}
