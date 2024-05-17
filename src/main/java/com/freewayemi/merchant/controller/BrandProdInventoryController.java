package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.BrandProductsInventoryBO;
import com.freewayemi.merchant.dto.response.BrandInventoryProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandProdInventoryController {
    private final BrandProductsInventoryBO brandProductsInventoryBO;

    @Autowired
    public BrandProdInventoryController(BrandProductsInventoryBO brandProductsInventoryBO) {
        this.brandProductsInventoryBO = brandProductsInventoryBO;
    }

    @GetMapping("/api/v1/brand/{brandId}/inventoryProducts")
    public List<BrandInventoryProductResponse> get(@PathVariable("brandId") String brandId) {
        return brandProductsInventoryBO.searchProducts(brandId);
    }
}
