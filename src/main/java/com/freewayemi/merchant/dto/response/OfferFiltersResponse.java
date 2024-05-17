package com.freewayemi.merchant.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OfferFiltersResponse {
    List<BrandResponse> brands;
    List<ProductCategory> categories;
    OfferBankDTO banks;
    List<String> segmentOffers;
}
