package com.freewayemi.merchant.commons.dto.offer;

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
