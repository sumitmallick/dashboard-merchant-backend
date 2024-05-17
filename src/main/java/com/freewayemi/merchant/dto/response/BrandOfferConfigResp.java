package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.BrandOfferConfig;
import lombok.Data;

import java.time.Instant;

@Data
public class BrandOfferConfigResp {
    private String brandOfferId;
    private String brandId;
    private Boolean isValid;
    private Boolean isStoreOffer;
    private Boolean isBankOffer;
    private Instant validFrom;
    private Instant validTo;
    private String offerImage;
    private String bankName;
    private String productName;
    private String productNameMapped;
    private String description;

    public BrandOfferConfigResp(BrandOfferConfig brandOfferConfig) {
        this.brandOfferId = brandOfferConfig.getId().toString();
        this.brandId = brandOfferConfig.getBrandId();
        this.isStoreOffer = brandOfferConfig.getIsStoreOffer();
        this.isBankOffer = brandOfferConfig.getIsBankOffer();
        this.isValid = brandOfferConfig.getIsValid();
        this.validFrom = brandOfferConfig.getValidFrom();
        this.validTo = brandOfferConfig.getValidTo();
        this.offerImage = brandOfferConfig.getOfferImage();
        this.bankName = brandOfferConfig.getBankName();
        this.productName = brandOfferConfig.getProductName();
        this.productNameMapped = brandOfferConfig.getProductNameMapped();
        this.description = brandOfferConfig.getDescription();
    }
}
