package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "brands_products")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandProduct extends BaseEntity {
    private String uuid;
    private String brandId;
    private String product;
    private String variant;
    private String modelNo;
    private String skuCode;
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
    private String nBFCSchemeID;
    private Float maxMarginDpAmount;
    private Float minMarginDpAmount;
    private Instant validFrom;
    private Instant validTo;
}
