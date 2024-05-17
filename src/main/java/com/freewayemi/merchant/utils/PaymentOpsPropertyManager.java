package com.freewayemi.merchant.utils;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PaymentOpsPropertyManager {
    private final String paymentOpsApiKeys;
    private final String paymentOpsAuthKeys;
    private final String paymentOpsBaseUrl;

    public PaymentOpsPropertyManager(@Value("${paymentops.api.key}") String paymentOpsApiKeys,
                                     @Value("${paymentops.auth.key}")String paymentOpsAuthKeys,
                                     @Value("${payment.paymentops.url}")String paymentOpsBaseUrl){
        this.paymentOpsApiKeys=paymentOpsApiKeys;
        this.paymentOpsAuthKeys=paymentOpsAuthKeys;
        this.paymentOpsBaseUrl=paymentOpsBaseUrl;
    }
}
