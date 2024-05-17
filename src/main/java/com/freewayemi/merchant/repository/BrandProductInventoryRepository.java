package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandProductsInventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface BrandProductInventoryRepository extends MongoRepository<BrandProductsInventory, String> {
    Optional<List<BrandProductsInventory>> findByBrandId(String brandId);
}
