package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.StoreUserReward;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StoreUserRewardRepository extends MongoRepository<StoreUserReward, String> {
    Optional<List<StoreUserReward>> findByStoreUserId(String storeUserId);
    long countByStoreUserId(String storeUserId);
}

