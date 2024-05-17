package com.freewayemi.merchant.commons.dto.deliveryorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetails {

    private final String brand;
    private final String schemeName;
    private final String assetCategory;
    private final String product;
    private final String assetQuantity;
    private final String model;
    private final String productType;
    private final String imeiNumber;
    private final String noOfAdvanceEmi;

    @JsonCreator
    public ProductDetails(@JsonProperty("brand") String brand,
                          @JsonProperty("schemeName") String schemeName,
                          @JsonProperty("assetCategory") String assetCategory,
                          @JsonProperty("product") String product,
                          @JsonProperty("assetQuantity") String assetQuantity,
                          @JsonProperty("model") String model,
                          @JsonProperty("productType") String productType,
                          @JsonProperty("imeiNumber") String imeiNumber,
                          @JsonProperty("noOfAdvanceEmi") String noOfAdvanceEmi) {
        this.brand = brand;
        this.schemeName = schemeName;
        this.assetCategory = assetCategory;
        this.product = product;
        this.assetQuantity = assetQuantity;
        this.model = model;
        this.productType = productType;
        this.imeiNumber = imeiNumber;
        this.noOfAdvanceEmi = noOfAdvanceEmi;
    }

}
