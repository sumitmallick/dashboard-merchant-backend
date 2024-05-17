package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantNudge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MerchantNudgeRepository extends MongoRepository<MerchantNudge, String> {
    @Query("{'merchantId': ?0, 'type' : ?1, 'isRead': ?2, 'readAt': {$gte: ?3}}")
    Optional<MerchantNudge> todaysNudge(String merchantId, String type, Boolean isRead, Instant date);

    @Query("{'merchantId': ?0, 'type' : ?1, 'isRead': ?2, 'readAt': {$gte: ?3}, 'nudgeId': ?4}")
    Optional<List<MerchantNudge>> findNudgesShownToUser(String merchantId, String type, Boolean isRead, Instant date, String nudgeId);
}
