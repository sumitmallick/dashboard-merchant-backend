package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.commons.entity.MerchantDiscountRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantDiscountRateRepository extends MongoRepository<MerchantDiscountRate, String> {
    Optional<List<MerchantDiscountRate>> findByBrandId(String brandId);
}
