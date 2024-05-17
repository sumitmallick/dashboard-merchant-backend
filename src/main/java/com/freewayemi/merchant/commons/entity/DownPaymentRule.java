package com.freewayemi.merchant.commons.entity;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownPaymentRule {

    private final String cardType;
    private final String bankCode;
    
}
