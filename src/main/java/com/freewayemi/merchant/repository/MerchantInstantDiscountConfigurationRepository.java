package com.freewayemi.merchant.repository;


import com.freewayemi.merchant.entity.MerchantInstantDiscountConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MerchantInstantDiscountConfigurationRepository extends MongoRepository<MerchantInstantDiscountConfiguration, String> {
    @Query("{'merchantId': ?0, 'status' : ?1}")
    Optional<List<MerchantInstantDiscountConfiguration>> findByMerchantId(String merchantId, String status);

    @Query("{'brandId' : ?0 , 'status' : ?1}")
    Optional<List<MerchantInstantDiscountConfiguration>> findByBrandId(String brandId, String status);

    @Query("{'merchantId': ?0, 'brandId' : ?1 , 'status' : ?2}")
    Optional<MerchantInstantDiscountConfiguration> findByMerchantIdAndBrandId(String merchantId, String brandId, String status);
}