package com.freewayemi.merchant.commons.dto.payout;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundPayoutResponse {

    private final Integer statusCode;
    private final String status;
    private final String statusMessage;
    private final String payoutTransactionId;
    private final String refundPayoutOrderId;
    private final String orderId;

    @JsonCreator
    public RefundPayoutResponse(@JsonProperty("statusCode") Integer statusCode,
                                @JsonProperty("status") String status,
                                @JsonProperty("statusMessage") String statusMessage,
                                @JsonProperty("payoutTransactionId") String payoutTransactionId,
                                @JsonProperty("refundPayoutOrderId") String refundPayoutOrderId,
                                @JsonProperty("orderId") String orderId) {

        this.statusCode = statusCode;
        this.status = status;
        this.statusMessage = statusMessage;
        this.payoutTransactionId = payoutTransactionId;
        this.refundPayoutOrderId = refundPayoutOrderId;
        this.orderId = orderId;
    }

}
