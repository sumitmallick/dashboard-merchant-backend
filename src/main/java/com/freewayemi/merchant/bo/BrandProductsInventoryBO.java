package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.response.BrandInventoryProductResponse;
import com.freewayemi.merchant.entity.BrandProductsInventory;
import com.freewayemi.merchant.repository.BrandProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandProductsInventoryBO {
    private final BrandProductInventoryRepository brandProductInventoryRepository;

    @Autowired
    public BrandProductsInventoryBO(BrandProductInventoryRepository brandProductInventoryRepository) {
        this.brandProductInventoryRepository = brandProductInventoryRepository;
    }

    public List<BrandInventoryProductResponse> searchProducts(String brandId) {
        List<BrandProductsInventory> brandProducts =
                brandProductInventoryRepository.findByBrandId(brandId).orElse(new ArrayList<>());
        return brandProducts.stream()
                .map(item -> BrandInventoryProductResponse.builder().label(item.getProductName())
                        .value(item.getProductName()).build()).collect(Collectors.toList());
    }
}
