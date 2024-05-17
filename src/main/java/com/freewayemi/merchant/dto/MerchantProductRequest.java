package com.freewayemi.merchant.dto;

import lombok.Data;

@Data
public class MerchantProductRequest {
    private String merchantId;
    private String productName;
    private Float productPrice;
    private String productCategory;
}