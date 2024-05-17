package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateProductSkuRequest {
    private String merchantId;
    private String consumerId;
    private String productId;
    private String consumerMobile;
    private String serialNumber;
    private String modelNumber;
    private String brand;
    private String status;
    private String stage;
    private String transactionId;
    private Integer offerTenure;
    private Integer advanceEmiTenure;
    private String bank;
    private String brandId;
    private PaymentInfo paymentInfo;
}
