package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantProductResponse {
    private String uuid;
    private String merchantId;
    private String productName;
    private String productCategory;
    private Float productPrice;
    private String productId;
    private List<String> productImages;
    private Boolean active;
    private Boolean gstIncluded;
    private String _id;
}
