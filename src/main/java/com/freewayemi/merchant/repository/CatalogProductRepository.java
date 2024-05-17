package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CatalogProductRepository extends MongoRepository<MerchantProduct, String> {
    Optional<List<MerchantProduct>> findAllByMerchantIdAndActive(String merchantId, Boolean active);
    Optional<MerchantProduct> findByMerchantIdAndUuidAndActive(String merchantId, String uuid, Boolean active);

    @Query("{ 'merchantId': ?0, 'productName': {'$regex': ?1, '$options': 'i'}}")
    Optional<List<MerchantProduct>> findProductsByName(String merchantId, String text, Pageable pageable);
}
