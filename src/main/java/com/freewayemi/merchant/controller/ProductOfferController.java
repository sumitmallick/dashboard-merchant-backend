package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.ProductOfferBO;
import com.freewayemi.merchant.dto.request.ProductOfferRequestDTO;
import com.freewayemi.merchant.dto.response.OfferFiltersRequest;
import com.freewayemi.merchant.dto.response.OfferFiltersResponse;
import com.freewayemi.merchant.dto.response.ProductOfferResponse;
import com.freewayemi.merchant.dto.response.ProductOfferResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductOfferController {

    private final ProductOfferBO productOfferBO;

    @Autowired
    public ProductOfferController(ProductOfferBO productOfferBO) {
        this.productOfferBO = productOfferBO;
    }

    @PostMapping("/api/v1/offers/filters")
    public OfferFiltersResponse getOffersFilters(@RequestBody OfferFiltersRequest offerFiltersRequest)  {
        return productOfferBO.getOffersFilterV2(offerFiltersRequest);
    }

    @PostMapping("/api/v1/offers/productOffers")
    public ProductOfferResponseDTO getProductOffers(@RequestBody ProductOfferRequestDTO productOfferRequestDTO
    ) {
        return productOfferBO.getProductOffers(productOfferRequestDTO);
    }

    @PostMapping("/api/v1/offers/productOffers/offerCard")
    public List<ProductOfferResponse> getProductOffersByOfferCard(@RequestBody ProductOfferRequestDTO productOfferRequest
    ) {
        return productOfferBO.getProductOffersByOfferCard(productOfferRequest);
    }

    @PutMapping("/api/v1/offers/productOffers")
    public void updateProductOffers(@RequestParam(value = "brandId", required = false) String brandId,
                                    @RequestParam(value = "authCode") String authCode) {
         productOfferBO.updateProductOffersV2(brandId, authCode);
    }

    @GetMapping("/api/v1/offers/productOffers/offerCard/{offerCardId}/filters")
    public OfferFiltersResponse getProductOffersFilterByOfferCard(@PathVariable(value = "offerCardId") String offerCardId) {
        return productOfferBO.getProductOffersFilterByOfferCard(offerCardId);
    }

}
