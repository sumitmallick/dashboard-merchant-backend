package com.freewayemi.merchant.dto.response;

import lombok.Data;


@Data
public class OfferFiltersRequest {
    String merchantId;
    String[] banks;
    String[] categories;
    OfferFilterOfBrand filtersOfBrands;
}