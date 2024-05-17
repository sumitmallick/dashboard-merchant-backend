package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Commercial {
    private String type;
    private Integer maxTenure;
    private Boolean lowCostEmi;
}
