package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.BrandProduct;
import lombok.Data;

@Data
public class Variant {
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
    private Float maxMarginDpAmount;
    private Float minMarginDpAmount;

    public Variant(BrandProduct brandProduct){
        this.brandProductId = String.valueOf(brandProduct.getId());
        this.name = brandProduct.getVariant();
        this.modelNo = brandProduct.getModelNo();
        this.amount = brandProduct.getAmount();
        this.emiOption = brandProduct.getEmiOption();
        this.minAmount = brandProduct.getMinAmount();
        this.displayHeader = brandProduct.getDisplayHeader();
        this.displaySubHeader = brandProduct.getDisplaySubHeader();
        this.icon = brandProduct.getIcon();
        this.category = brandProduct.getCategory();
        this.isPopular = brandProduct.getIsPopular();
        this.isValid = brandProduct.getIsValid();
        this.imageUrl = brandProduct.getImageUrl();
        this.popularityScore = brandProduct.getPopularityScore();
        this.brandId = brandProduct.getBrandId();
        this.maxMarginDpAmount = brandProduct.getMaxMarginDpAmount();
        this.minMarginDpAmount = brandProduct.getMinMarginDpAmount();
    }
}
