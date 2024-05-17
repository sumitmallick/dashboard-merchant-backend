package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CatalogProductRequest {
    private final String productName;
    private final String productCategory;
    private final Float productPrice;
    private final String productId;
    private final List<String> productImages;
    private final Boolean gstIncluded;
}
