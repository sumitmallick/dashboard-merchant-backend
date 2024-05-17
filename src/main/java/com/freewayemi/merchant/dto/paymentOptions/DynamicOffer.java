package com.freewayemi.merchant.dto.paymentOptions;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DynamicOffer {
    private Integer tenure;
    private String cardType;
    private Float rate;
    private Boolean selected;
}
