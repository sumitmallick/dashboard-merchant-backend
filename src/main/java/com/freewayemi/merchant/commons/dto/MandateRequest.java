package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MandateRequest {
    private String mobile;
    private String email;
    private String orderId;
    private String firstName;
    private String lastName;
    private Float amount;
    private MandatePaymentDetails paymentDetails;
    private String mandateProvider;
    private String returnUrl;
    private String prospectId;
    private String loanReferenceId;
    private String applicationNumber;
    private String lenderCode;
}