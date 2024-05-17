package com.freewayemi.merchant.repository;
import com.freewayemi.merchant.entity.MerchantConfigs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MerchantConfigsRepository extends MongoRepository<MerchantConfigs, String> {
    @Query("{'label': ?0}")
    Optional<MerchantConfigs> findByLabel(String label);
}
