package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductOfferVariant {
    private String brandProductId;
    private String name;
    private String modelNo;
    private Float amount;
    private String emiOption;
    private Float minAmount;
    private String displayHeader;
    private String displaySubHeader;
    private String icon;
    private String category;
    private Boolean isValid;
    private Boolean isPopular;
    private String imageUrl;
    private String popularityScore;
    private String brandName;
    private String brandId;
    private String brandIcon;
    private String productName;
}
