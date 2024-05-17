package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Merchandise {
    private Boolean deployedMerchandise;
    private Boolean isExclusive;
    private String noDeployedReason;
    private List<String> selectedCollateralOptions;
}
