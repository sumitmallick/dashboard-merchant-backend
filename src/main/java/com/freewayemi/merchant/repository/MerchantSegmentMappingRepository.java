package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantSegmentMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MerchantSegmentMappingRepository extends MongoRepository<MerchantSegmentMapping, String> {

    @Query("{'merchantId': ?0, '$and': [" +
            "{'$or': [{'validFrom': {'$exists': false}}, {'validFrom': {'$lte': ?1 }}]}," +
            "{'$or': [{'validTo': {'$exists': false}}, {'validTo': {'$gte': ?1 }}]}," +
            "{'$or': [{'isValid': {'$exists': false}}, {'isValid': ?2 }]}" +
            "]}")
    Optional<List<MerchantSegmentMapping>> findByMerchantIdAndIsValid(String merchantId, Instant time, Boolean isValid);

}
