package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantProperties;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantProperitiesRepository extends MongoRepository<MerchantProperties, String> {
    Optional<List<MerchantProperties>> findByMerchantId(String merchantId);
}
