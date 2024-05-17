package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.response.ProductOfferVariant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "product_offers")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductOffer extends BaseEntity {
    private String offerId;
    private String productOfferCardId;
    private String type;
    private  List<String> preApprovedCard;
    private  List<String> creditCard;
    private Float subvention;
    private Float cashback;
    private Float additionalCashback;
    private Float totalCashback;
    private Instant validFrom;
    private Instant validTo;
    private String offerDescription;
    private Boolean isValid;
    private Float maxOfferAmount;
    private Float offerPercentage;
    private ProductOfferVariant variant;
    private List<Integer> tenures;
    private String emiType;
    private String segmentId;
    private String partner;
}
