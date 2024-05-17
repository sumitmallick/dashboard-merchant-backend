package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Target {
    private Integer obTargetFTD;
    private Integer obTargetMTD;
    private Integer volTargetFTD;
    private Integer volTargetMTD;
}
