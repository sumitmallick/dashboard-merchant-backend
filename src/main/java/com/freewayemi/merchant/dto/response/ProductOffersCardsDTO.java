package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.ProductOffer;
import lombok.Data;

import java.time.Instant;
import java.util.*;


@Data
public class ProductOffersCardsDTO {
    private String brandId;
    private String category;
    private String type;
    private String emiType;
    private Float cashback;
    private Float subvention;
    private Float offerPercentage;
    private Float maxOfferAmount;
    private Map<Integer, Integer> tenures;
    private Map<String, String> banks;
    private Map<String, String> creditCards;
    private Map<String ,ProductOffer> products;
    private Map<String ,String> clubProducts;
    private Instant validFrom;
    private Instant validTo;
    private boolean isNoCostEMIUpdated;
    private List<String> offerIds;
    private List<ProductOffer> productOfferList;
    private String segmentId;
    private String partner;


    public ProductOffersCardsDTO(ProductOffer productOffers) {
        this.type = productOffers.getType();
        this.brandId = productOffers.getVariant().getBrandId();
        this.category = productOffers.getVariant().getCategory();
        this.maxOfferAmount = productOffers.getMaxOfferAmount();
        this.offerPercentage = productOffers.getOfferPercentage();
        this.cashback = productOffers.getCashback();
        this.subvention = productOffers.getSubvention();
        this.tenures = new Hashtable<>();
        this.banks = new Hashtable<>();
        this.creditCards = new Hashtable<>();
        this.clubProducts= new HashMap<>();
        this.products= new HashMap<>();
        this.validTo = productOffers.getValidTo();
        this.validFrom = productOffers.getValidFrom();
        this.emiType = productOffers.getEmiType();
        this.offerIds = new ArrayList<>();
        this.productOfferList = new ArrayList<>();
        this.segmentId = productOffers.getSegmentId();
        this.partner = productOffers.getPartner();
    }
}
