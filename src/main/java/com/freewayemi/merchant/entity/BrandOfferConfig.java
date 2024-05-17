package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "brand_offer_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandOfferConfig extends BaseEntity {
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
}
