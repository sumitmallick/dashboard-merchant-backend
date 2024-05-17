package com.freewayemi.merchant.commons.ntbservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NtbLoanDto {
    private final Float amount;
    private final String prospectId;
    private final Integer tenure;
    private final String transactionId;
    private final String mobileNumber;
    private final String provider;
    private final String pan;
    private final String updateType;
    private final Float emi;
    private final Float downpaymentAmount;
    private final Float discount;
    private final Float pgAmount;
    private final Float roi;
    private final Float processingFee;
    private final Float gstOnProcessingFee;
    private final String ip;
    private final String longitude;
    private final String latitude;
    private final String cancellationReason;
    private final String refundAmount;
    private final String disbursalReferenceNumber;
    private final String refundId;
}
