package com.freewayemi.merchant.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductOfferRequestDTO {
    private  String[] categories;
    private  String[] brands;
    private  OfferBankRequest banks;
    private  String searchText;
    private  String merchantId;
    private  String productOfferCardId;
    private  Integer limit;
    private  Integer offset;
    private  Boolean isHighToLow;
    private boolean isBrandSubventionModel;
    private List<String> segmentOffers;
    private String partner;
}
