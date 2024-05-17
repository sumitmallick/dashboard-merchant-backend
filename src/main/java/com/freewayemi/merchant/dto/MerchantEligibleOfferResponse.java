package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MerchantEligibleOfferResponse {
    Map<String, Map<String, List<MerchantPriceResponse>>> cardOffers;
}
