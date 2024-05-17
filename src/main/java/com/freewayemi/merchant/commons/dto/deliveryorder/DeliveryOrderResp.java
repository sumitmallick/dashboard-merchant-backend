package com.freewayemi.merchant.commons.dto.deliveryorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryOrderResp {

    private final Integer statusCode;
    private final String status;
    private final String statusMessage;
    private final String date;
    private final String orderId;
    private final String paymentTxnId;
    private final StoreDetails storeDetails;
    private final ProductDetails productDetails;
    private final ChargeInfo chargeInfo;
    private final String pdfFile;

    @JsonCreator
    public DeliveryOrderResp(@JsonProperty("statusCode") Integer statusCode, @JsonProperty("status") String status,
                             @JsonProperty("statusMessage") String statusMessage, @JsonProperty("date") String date,
                             @JsonProperty("orderId") String orderId, @JsonProperty("paymentTxnId") String paymentTxnId,
                             @JsonProperty("storeDetails") StoreDetails storeDetails,
                             @JsonProperty("productDetails") ProductDetails productDetails,
                             @JsonProperty("chargeInfo") ChargeInfo chargeInfo,
                             @JsonProperty("pdfFile") String pdfFile) {
        this.statusCode = statusCode;
        this.status = status;
        this.statusMessage = statusMessage;
        this.date = date;
        this.orderId = orderId;
        this.paymentTxnId = paymentTxnId;
        this.storeDetails = storeDetails;
        this.productDetails = productDetails;
        this.chargeInfo = chargeInfo;
        this.pdfFile = pdfFile;
    }

}
