package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CatalogProduct {
    private final String uuid;
    private final String productName;
    private final String productCategory;
    private final Float productPrice;
    private final String productId;
    private final List<String> productImages;
    private final Boolean gstIncluded;
}
