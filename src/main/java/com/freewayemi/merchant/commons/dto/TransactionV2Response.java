package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class TransactionV2Response {
    private final String orderId;
    private final String paymentTxnId;
    private final String status;
    private final Integer statusCode;
    private final String statusMessage;
    private final String settlementStatus;
    private final Consumer consumer;
    private final Charge charge;
    private final Product product;
    private final Payment payment;
    private final String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<RefundInfo> refunds;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String refundSettlementStatus;
    private final Map<String, String> customParams;
    private final ProviderInfo providerInfo;
    private final String merchantDisplayId;

    public TransactionV2Response(TransactionResponse tr) {
        this.orderId = tr.getMerchantOrderId();
        this.paymentTxnId = tr.getTxnId();
        this.status = tr.getStatus();
        this.statusCode = tr.getStatusCode();
        this.statusMessage = tr.getStatusMessage();
        this.settlementStatus = tr.getSettlementStatus();
        this.consumer = Consumer.builder()
                .mobile(tr.getMobile())
                .email(tr.getEmail())
                .consumerName(tr.getConsumerName())
                .address(tr.getAddress())
                .build();
        this.charge = Charge.builder()
                .amount(get(tr.getAmount()))
                .discount(get(tr.getDiscount()))
                .gst(get(tr.getGstCharges()))
                .settlement(get(tr.getSettlementAmount()))
                .netSettlement(get(tr.getNetSettlementAmount()))
                .invoice(get(tr.getInvoiceAmount()))
                .mdrCharges(get(tr.getMdrCharges()))
                .dbd(tr.getDbd())
                .dbdAmount(tr.getDbdAmount())
                .mbd(tr.getMbd())
                .mbdAmount(tr.getMbdAmount())
                .additionalCashbackAmount(tr.getAdditionalCashbackAmount())
                .totalCashback(tr.getTotalCashback())
                .build();
        this.product = Product.builder().name(tr.getProductName())
                .serialNumber(tr.getSerialNumber()).build();
        this.payment = Payment.builder()
                .bankName(tr.getBankName())
                .cardType(tr.getCardType())
                .emi(get(tr.getEmi()))
                .tenure(get(tr.getTenure()))
                .advanceEmiTenure(get(tr.getAdvanceEmiTenure()))
                .build();
        this.timestamp = tr.getDisplayDate();
        this.refunds = null == tr.getRefund() ? null : tr.getRefund()
                .getRefunds()
                .stream()
                .map(ri -> RefundInfo.builder()
                        .amount(ri.getAmount())
                        .status(ri.getStatus())
                        .statusCode(ri.getStatusCode())
                        .statusMessage(ri.getStatusMessage())
                        .refundTransactionId(ri.getRefundTransactionId())
                        .paymentRefundTransactionId(ri.getpaymentRefundTransactionId())
                        .settlementStatus(ri.getSettlementStatus())
                        .build())
                .collect(Collectors.toList());
        this.refundSettlementStatus = null == tr.getRefund() ? null : tr.getSettlementStatus();
        this.customParams = tr.getCustomParams();
        this.providerInfo = ProviderInfo.builder().bankReferenceNo(tr.getBankReferenceNo()).build();
        this.merchantDisplayId = tr.getMerchantDisplayId();

    }

    private String get(Float f) {
        return null == f ? null : f.toString();
    }

    private String get(Integer f) {
        return null == f ? null : f.toString();
    }

    @Data
    @JsonDeserialize(builder = RefundInfo.RefundInfoBuilder.class)
    @Builder(builderClassName = "RefundInfoBuilder", toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RefundInfo {
        private final Float amount;
        private final String status;
        private final Integer statusCode;
        private final String statusMessage;
        private final String refundTransactionId;
        private final String paymentRefundTransactionId;
        private final String settlementStatus;
        private final Integer refundTATDays;
        private final Instant txnSuccessDate;
        private final Long numberOfDaysSinceTransaction;
        private final Instant refundCutOffDate;
        private final String error;
        private final String paymentTransactionId;


        @JsonPOJOBuilder(withPrefix = "")
        public static class RefundInfoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Consumer.ConsumerBuilder.class)
    @Builder(builderClassName = "ConsumerBuilder", toBuilder = true)
    public static class Consumer {
        private final String mobile;
        private final String email;
        private final String consumerName;
        private final Address address;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ConsumerBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Charge.ChargeBuilder.class)
    @Builder(builderClassName = "ChargeBuilder", toBuilder = true)
    public static class Charge {
        private final String amount;
        private final String discount;
        private final String settlement;
        private final String gst;
        private final String netSettlement;
        private final String invoice;
        private final String mdrCharges;
        private final Double dbd;
        private final Double dbdAmount;
        private final Double mbd;
        private final Double mbdAmount;
        private final Float additionalCashbackAmount;
        private final Float totalCashback;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ChargeBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Product.ProductBuilder.class)
    @Builder(builderClassName = "ProductBuilder", toBuilder = true)
    public static class Product {
        private final String name;
        private final String serialNumber;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ProductBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Payment.PaymentBuilder.class)
    @Builder(builderClassName = "PaymentBuilder", toBuilder = true)
    public static class Payment {
        private final String bankName;
        private final String cardType;
        private final String emi;
        private final String tenure;
        private final String advanceEmiTenure;

        @JsonPOJOBuilder(withPrefix = "")
        public static class PaymentBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = ProviderInfo.ProviderInfoBuilder.class)
    @Builder(builderClassName = "ProviderInfoBuilder", toBuilder = true)
    public static class ProviderInfo {
        private final String bankReferenceNo;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ProviderInfoBuilder {
        }
    }

}
