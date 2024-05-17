package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductOfferResponseDTO {
   private  List<ProductOfferResponse> productOffers;
   private Integer totalOfferCount;
}
