package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PgTransactionResponse {
    private final String orderId;
    private final String paymentTxnId;
    private final String paymentRedirectUrl;
    private final PriceResponse applicableOfferResponse;

    @JsonCreator
    public PgTransactionResponse(String orderId, String paymentTxnId, String paymentRedirectUrl,
                                 PriceResponse applicableOfferResponse) {
        this.orderId = orderId;
        this.paymentTxnId = paymentTxnId;
        this.paymentRedirectUrl = paymentRedirectUrl;
        this.applicableOfferResponse = applicableOfferResponse;
    }
}
