package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CommercialResponse {
    private Map<String, List<BrandCommercialMdr>> brandCommercials;
    private List<CommercialPojo> commercials;
}
