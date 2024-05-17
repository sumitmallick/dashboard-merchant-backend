package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionSettlementResponse {
    private String settlementId;
    private String paymentTxnId;
    private String originalAmount;
    private String mdr;
    private String gstAmount;
    private String netSettlementAmt;
    private Instant settlementDate;
    private String utrNo;
    private String paymentRefundId;
    private String merchantId;

    @JsonCreator
    public TransactionSettlementResponse(@JsonProperty("settlementId") String settlementId,
                                         @JsonProperty("displayId") String displayId,
                                         @JsonProperty("merchantName") String merchantName,
                                         @JsonProperty("paymentMode") String paymentMode,
                                         @JsonProperty("transactionType") String transactionType,
                                         @JsonProperty("transactionDateAndTime") String transactionDateAndTime,
                                         @JsonProperty("merchantOrderId") String merchantOrderId,
                                         @JsonProperty("paymentTxnId") String paymentTxnId,
                                         @JsonProperty("originalAmount") String originalAmount,
                                         @JsonProperty("transactionCurrency") String transactionCurrency,
                                         @JsonProperty("mdr") String mdr,
                                         @JsonProperty("gstAmount") String gstAmount,
                                         @JsonProperty("netSettlementAmt") String netSettlementAmt,
                                         @JsonProperty("settlementStatus") String settlementStatus,
                                         @JsonProperty("settlementDate") Instant settlementDate,
                                         @JsonProperty("gstNumber") String gstNumber,
                                         @JsonProperty("tenure") String tenure,
                                         @JsonProperty("payoutBank") String payoutBank,
                                         @JsonProperty("utrNo") String utrNo,
                                         @JsonProperty("paymentRefundId") String paymentRefundId,
                                         @JsonProperty("merchantId") String merchantId) {
        this.settlementId = settlementId;
        this.paymentTxnId = paymentTxnId;
        this.originalAmount = originalAmount;
        this.mdr = mdr;
        this.gstAmount = gstAmount;
        this.netSettlementAmt = netSettlementAmt;
        this.settlementDate = settlementDate;
        this.utrNo = utrNo;
        this.paymentRefundId = paymentRefundId;
        this.merchantId = merchantId;
    }
}
