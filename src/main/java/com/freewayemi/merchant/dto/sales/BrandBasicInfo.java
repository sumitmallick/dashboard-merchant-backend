package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandBasicInfo {
    private String name;
    private String icon;
    private String id;
}
