package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandInventoryProductResponse {
    private final String label;
    private final String value;
}
