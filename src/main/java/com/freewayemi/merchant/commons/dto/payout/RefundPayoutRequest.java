package com.freewayemi.merchant.commons.dto.payout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundPayoutRequest {

    private final String orderId;
    private final String refundPayoutOrderId;
    private final String transactionId;
    private final Float amount;
    private final PaymentDetailsDto paymentDetails;
    private final String webhookUrl;
    private final String createdBy;
    private String source;

    @JsonCreator
    public RefundPayoutRequest(@JsonProperty("orderId") String orderId,
                               @JsonProperty("refundPayoutOrderId") String refundPayoutOrderId,
                               @JsonProperty("transactionId") String transactionId,
                               @JsonProperty("amount") Float amount,
                               @JsonProperty("paymentDetails") PaymentDetailsDto paymentDetails,
                               @JsonProperty("webhookUrl") String webhookUrl,
                               @JsonProperty("createdBy") String createdBy,
                               @JsonProperty("source") String source) {

        this.orderId = orderId;
        this.refundPayoutOrderId = refundPayoutOrderId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentDetails = paymentDetails;
        this.webhookUrl = webhookUrl;
        this.createdBy = createdBy;
        this.source = source;
    }

}
