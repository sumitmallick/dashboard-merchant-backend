package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesAnalytics {
    private MerchantStatus merchantStatus;
    private HomeAnalytics homeAnalytics;
    private HomeV1Analytics homeV1Analytics;
}
