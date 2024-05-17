package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.entity.BrandProduct;
import lombok.Data;

@Data
public class BrandProductsResponse {
    private String name;
    private String brandProductId;
    private String variant;
    private String modelNo;
    private Float amount;
    private String emiOption;
    private Float minAmount;
    private String displayHeader;
    private String displaySubHeader;
    private Float maxMarginDpAmount;
    private Float minMarginDpAmount;
    private String category;
    private String brandName;

    public BrandProductsResponse(BrandProduct brandProduct, String brandName) {
        this.name = Util.isNotNull(brandProduct) ? brandProduct.getProduct() : null;;
        this.brandProductId = Util.isNotNull(brandProduct) ? String.valueOf(brandProduct.getId()) : null;
        this.variant = Util.isNotNull(brandProduct) ? brandProduct.getVariant() : null;
        this.modelNo = Util.isNotNull(brandProduct) ? brandProduct.getModelNo() : null;
        this.amount = Util.isNotNull(brandProduct) ? brandProduct.getAmount() : null;
        this.emiOption = Util.isNotNull(brandProduct) ? brandProduct.getEmiOption() : null;
        this.minAmount = Util.isNotNull(brandProduct) ? brandProduct.getMinAmount() : null;
        this.displayHeader = Util.isNotNull(brandProduct) ? brandProduct.getDisplayHeader() : null;
        this.displaySubHeader = Util.isNotNull(brandProduct) ? brandProduct.getDisplaySubHeader() : null;
        this.maxMarginDpAmount = Util.isNotNull(brandProduct) ? brandProduct.getMaxMarginDpAmount() : null;
        this.minMarginDpAmount = Util.isNotNull(brandProduct) ? brandProduct.getMinMarginDpAmount() : null;
        this.category = Util.isNotNull(brandProduct) ? brandProduct.getCategory() : null;
        this.brandName = Util.isNotNull(brandName) ? brandName : null;
    }
}
