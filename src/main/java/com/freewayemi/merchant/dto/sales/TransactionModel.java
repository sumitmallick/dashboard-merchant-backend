package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class TransactionModel {
    private  String transactionId;
    private  String shopName;
    private  String merchantMobile;
    private  String merchantEmail;
    private  String statusMessage;
    private  String status;
    private  Float amount;
    private  Instant createdDate;
    private  String cardType;
    private  String bankName;
    private  String utrNo;
    private  List<EligibilityResponse> eligibilities;
    private  String customerMobile;
    private  String customerEmail;
}
