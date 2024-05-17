package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandCommercialMdr {
    private String cardType;
    private String bankCode;
    private Integer tenure;
    private String rate;
    private Float maxRate;

}
