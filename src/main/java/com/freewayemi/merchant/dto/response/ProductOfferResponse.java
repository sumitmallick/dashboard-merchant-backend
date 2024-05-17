package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.ProductOffer;
import com.freewayemi.merchant.entity.ProductOfferCard;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductOfferResponse {
    private  String type;
    private  String emiType;
    private String productOfferCardId;
    private  Integer productOfferCount;
    private  List<ProductOfferResponse> productOffers;
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

    public ProductOfferResponse(ProductOffer productOffers, ProductOffersCardsDTO productOffersCardsDTO) {
        this.variant = productOffers.getVariant();
        this.type = productOffers.getType();
        this.productOfferCount = productOffersCardsDTO.getProducts().size();
        this.subvention = productOffers.getSubvention();
        this.cashback = productOffers.getCashback();
        this.additionalCashback = productOffers.getAdditionalCashback();
        this.maxOfferAmount = productOffers.getMaxOfferAmount();
        this.offerPercentage = productOffers.getOfferPercentage();
        this.validFrom = productOffers.getValidFrom();
        this.validTo = productOffers.getValidTo();
        this.offerDescription = productOffers.getOfferDescription();
        this.isValid = productOffers.getIsValid();
        this.preApprovedCard = new ArrayList<>(productOffersCardsDTO.getBanks().values());
        this.creditCard = new ArrayList<>(productOffersCardsDTO.getCreditCards().values());
        this.tenures = new ArrayList<>(productOffersCardsDTO.getTenures().values());
        this.partner = productOffers.getPartner();
       }

    public ProductOfferResponse(ProductOfferCard productOfferCard) {
        this.productOfferCardId = productOfferCard.getId().toString();
        this.variant = productOfferCard.getVariant();
        this.type = productOfferCard.getType();
        this.productOfferCount = productOfferCard.getProductOfferCount();
        this.subvention = productOfferCard.getSubvention();
        this.cashback = productOfferCard.getCashback();
        this.additionalCashback = productOfferCard.getAdditionalCashback();
        this.maxOfferAmount = productOfferCard.getMaxOfferAmount();
        this.offerPercentage = productOfferCard.getOfferPercentage();
        this.validFrom = productOfferCard.getValidFrom();
        this.validTo = productOfferCard.getValidTo();
        this.offerDescription = productOfferCard.getOfferDescription();
        this.isValid = productOfferCard.getIsValid();
        this.preApprovedCard =productOfferCard.getPreApprovedCard();
        this.creditCard = productOfferCard.getCreditCard();
        this.tenures = productOfferCard.getTenures();
        this.emiType =  productOfferCard.getEmiType();
        this.segmentId = productOfferCard.getSegmentId();
        this.partner = productOfferCard.getPartner();
    }

    public ProductOfferResponse(ProductOffer productOffers, ProductOfferCard productOfferCard, int totalOfferCount) {
        this.productOfferCardId = productOfferCard.getId().toString();
        this.variant = productOffers.getVariant();
        this.type = productOffers.getType();
        this.productOfferCount = totalOfferCount;
        this.subvention = productOffers.getSubvention();
        this.cashback = productOffers.getCashback();
        this.additionalCashback = productOffers.getAdditionalCashback();
        this.maxOfferAmount = productOffers.getMaxOfferAmount();
        this.offerPercentage = productOffers.getOfferPercentage();
        this.validFrom = productOffers.getValidFrom();
        this.validTo = productOffers.getValidTo();
        this.offerDescription = productOffers.getOfferDescription();
        this.isValid = productOffers.getIsValid();
        this.preApprovedCard =  productOffers.getPreApprovedCard();
        this.creditCard = productOffers.getCreditCard();
        this.tenures = productOffers.getTenures();
        this.emiType =  productOffers.getEmiType();
        this.segmentId = productOffers.getSegmentId();
        this.partner = productOffers.getPartner();
    }
}
