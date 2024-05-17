package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.entity.SubCategory;
import lombok.Data;

import java.util.List;

@Data
public class MerchantCategoryResponse {
    private final String category;
    private final List<String> subCategories;
    private final List<SubCategory> subCategoryList;
    private final Integer order;

    @JsonCreator
    public MerchantCategoryResponse(@JsonProperty("category") String category,
                                    @JsonProperty("subCategories") List<String> subCategories,
                                    @JsonProperty("subCategoryList") List<SubCategory> subCategoryList,
                                    @JsonProperty("order") Integer order) {
        this.category = category;
        this.subCategories = subCategories;
        this.subCategoryList = subCategoryList;
        this.order = order;
    }
}
