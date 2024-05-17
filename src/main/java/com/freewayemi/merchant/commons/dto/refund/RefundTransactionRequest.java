package com.freewayemi.merchant.commons.dto.refund;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
public class RefundTransactionRequest {

    @NotNull(message = "Please provide unique refund id.")
    private final String refundTransactionId;

    private final String paymentRefundTransactionId;

    @NotNull(message = "Please provide amount.")
    private Float amount;
    private Map<String, String> additionalInfo;
    private String createdBy;

    @JsonCreator
    public RefundTransactionRequest(@JsonProperty("refundTransactionId") String refundTransactionId,
                                    @JsonProperty("paymentRefundTransactionId") String paymentRefundTransactionId,
                                    @JsonProperty("amount") Float amount,
                                    @JsonProperty("additionalInfo") Map<String, String> additionalInfo,
                                    @JsonProperty("createdBy") String createdBy) {
        this.refundTransactionId = refundTransactionId;
        this.paymentRefundTransactionId = paymentRefundTransactionId;
        this.amount = amount;
        this.additionalInfo = additionalInfo;
        this.createdBy = createdBy;
    }

}
