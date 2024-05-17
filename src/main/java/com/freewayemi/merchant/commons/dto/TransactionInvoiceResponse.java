package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionInvoiceResponse {
    private final String txnInvoiceId;
    private final String transactionId;
    private final String invoiceNumber;
    private final String bankName;
    private final String cardType;
    private final String productName;
    private final String amount;
    private final Instant txnSuccessDate;
    private final String mobile;
    private String url;

    @JsonCreator
    public TransactionInvoiceResponse(@JsonProperty("txnInvoiceId") String txnInvoiceId,
                                      @JsonProperty("transactionId") String transactionId,
                                      @JsonProperty("invoiceNumber") String invoiceNumber,
                                      @JsonProperty("bankName") String bankName,
                                      @JsonProperty("cardType") String cardType,
                                      @JsonProperty("productName") String productName,
                                      @JsonProperty("amount") String amount,
                                      @JsonProperty("txnSuccessDate") Instant txnSuccessDate,
                                      @JsonProperty("mobile") String mobile,
                                      @JsonProperty("url") String url) {
        this.txnInvoiceId = txnInvoiceId;
        this.transactionId = transactionId;
        this.invoiceNumber = invoiceNumber;
        this.bankName = bankName;
        this.cardType = cardType;
        this.productName = productName;
        this.amount = amount;
        this.txnSuccessDate = txnSuccessDate;
        this.mobile = mobile;
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
