package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BrandProductRequestDto {
    private  String[] category;
    @NotNull(message = "Please provide limit.")
    private  Integer limit;
    @NotNull(message = "Please provide offset.")
    private  Integer offset;
    private  String searchProduct;
    private  Boolean isPopular;
    private  String cardType;
    private String barcodeText;

    @JsonCreator
    public BrandProductRequestDto(@JsonProperty("category")  String[] category,
                                  @JsonProperty("limit")  Integer limit,
                                  @JsonProperty("offset")   Integer offset,
                                  @JsonProperty("searchProduct")  String searchProduct,
                                  @JsonProperty("isPopular")  Boolean isPopular,
                                  @JsonProperty("cardType")  String cardType,
                                  @JsonProperty("barcodeText")  String barcodeText) {
        this.category = category;
        this.limit = limit;
        this.offset = offset;
        this.searchProduct = searchProduct;
        this.isPopular=isPopular;
        this.cardType=cardType;
        this.barcodeText = barcodeText;
    }
}
