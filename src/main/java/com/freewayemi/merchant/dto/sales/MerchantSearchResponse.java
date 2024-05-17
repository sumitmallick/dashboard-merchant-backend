package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantSearchResponse {
    private String shopName;
    private String displayId;
    private String merchantId;
}
