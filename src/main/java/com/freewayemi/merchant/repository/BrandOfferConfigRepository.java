package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandOfferConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BrandOfferConfigRepository extends MongoRepository<BrandOfferConfig, String> {

    @Query("{'brandId': { $in: ?0 }, 'validFrom' : {'$lte': ?1}, 'validTo': {'$gte': ?1}, 'isValid': ?2, 'isStoreOffer' : true}")
    Optional<List<BrandOfferConfig>> findStoreOfferByBrandIdAndIsActive(String[] brandIds, Instant currTime, Boolean isValid);

    @Query("{'brandId': { $in: ?0 }, 'validFrom' : {'$lte': ?1}, 'validTo': {'$gte': ?1}, 'isValid': ?2, 'isBankOffer' : true}")
    Optional<List<BrandOfferConfig>> findBrandOfferByBrandIdAndIsActive(String[] brandIds, Instant currTime, Boolean isValid);

}
