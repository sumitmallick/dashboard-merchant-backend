package com.freewayemi.merchant.repository;
import com.freewayemi.merchant.entity.MerchantConfigs;
import com.freewayemi.merchant.entity.MerchantConfigsV2;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MerchantConfigsV2Repository extends MongoRepository<MerchantConfigsV2, String> {
    @Query("{'label': ?0}")
    Optional<MerchantConfigsV2> findByLabel(String label);
}
