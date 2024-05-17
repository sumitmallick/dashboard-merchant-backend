package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BrandRequestDto {

    @NotNull(message = "Please provide category.")
    private  String[] category;
    @NotNull(message = "Please provide limit.")
    private  Integer limit;
    @NotNull(message = "Please provide offset.")
    private  Integer offset;
    @NotNull(message = "Please provide brand type.")
    private  String brandType;
    private  String searchBrandText;

    @JsonCreator
    public BrandRequestDto(@JsonProperty("category") String[] category,
                           @JsonProperty("limit") Integer limit,
                           @JsonProperty("offset") Integer offset,
                           @JsonProperty("brandType") String brandType,
                           @JsonProperty("searchBrandText") String searchBrandText) {
        this.category = category;
        this.limit = limit;
        this.offset = offset;
        this.brandType = brandType;
        this.searchBrandText = searchBrandText;
    }
}
