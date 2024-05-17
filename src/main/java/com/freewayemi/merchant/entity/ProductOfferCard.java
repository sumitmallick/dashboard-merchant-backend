package com.freewayemi.merchant.entity;


import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.response.ProductOfferVariant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "product_offers_cards")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductOfferCard extends BaseEntity {
    private  String type;
    private  String emiType;
    private  Integer productOfferCount;
    private  ProductOfferVariant variant;
    private  List<String> preApprovedCard;
    private  List<String> creditCard;
    private  List<Integer> tenures;
    private  Float subvention;
    private  Float cashback;
    private  Float additionalCashback;
    private  Float maxOfferAmount;
    private  Float offerPercentage;
    private  Instant validFrom;
    private  Instant validTo;
    private  String offerDescription;
    private  Boolean isValid;
    private String segmentId;
    private String partner;
}
