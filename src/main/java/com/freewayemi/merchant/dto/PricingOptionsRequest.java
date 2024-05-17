package com.freewayemi.merchant.dto;

import lombok.Getter;

@Getter
public class PricingOptionsRequest {
    private Float amount;
    private String consumerMobile;
    private String productId;
    private String brandId;
}
