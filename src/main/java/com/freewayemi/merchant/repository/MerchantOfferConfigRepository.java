package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantOfferConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MerchantOfferConfigRepository extends MongoRepository<MerchantOfferConfig, String> {
    Optional<MerchantOfferConfig> findByMerchantId(String merchantId);
}
