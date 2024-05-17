package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferRepository extends MongoRepository<Offer, String> {
    Optional<List<Offer>> findByMerchantId(String merchantId);

    @Query("{'brandId': ?0, 'validFrom' : {'$lte': ?1}, 'validTo': {'$gte': ?1}, 'isValid': ?2, 'partner': ?3}")
    Optional<List<Offer>> findByBrandId(String brandId, Instant time, Boolean isValid, String partner);
    @Query("{'brandId': ?0, 'type' : ?1 , '$or':[ {'productId': 'any'},{ 'productIds':?2}], " +
            "'validFrom' : {'$lte': ?3}, 'validTo': {'$gte': ?3}, 'isValid': ?4, 'partner': ?5}")
    Optional<List<Offer>> findByBrandIdAndTypeAndProductIdIsValid(String brandId, String type, String productId,
                                                                  Instant currTime, Boolean isValid, String partner);

    @Query("{'brandId': ?0, 'cardType' : ?1 , " +
            "'validFrom' : {'$lte': ?2}, 'validTo': {'$gte': ?2}, 'isValid': ?3, 'partner': ?4}")
    Optional<List<Offer>> findByBrandIdAndCardTypeAndIsValid(String brandId, String cardType, Instant currTime,
                                                             Boolean isValid, String partner);

    @Query("{'brandId': ?0, 'type' : {$exists: true}, 'type' :{ $ne : 'brandBankAdditionalCashback'}, " +
            "'validFrom' : {$exists: true}, 'validFrom' : {'$lte': ?1}, 'validTo' : {$exists: true}," +
            " 'validTo': {'$gte': ?1}, 'isValid' : {$exists: true}, 'isValid': ?2, 'partner': ?3}")
    Optional<List<Offer>> findByBrandIdAndIsValidAndNotAdditionalCashback(String brandId, Instant currTime,
                                                                          Boolean isValid, String partner);

    @Query(value = "{'brandId': ?0, 'type' : {$exists: true}, 'type' :{ $ne : 'brandBankAdditionalCashback'}, " +
            "'validFrom' : {$exists: true}, 'validFrom' : {'$lte': ?1}, 'validTo' : {$exists: true}," +
            " 'validTo': {'$gte': ?1}, 'isValid' : {$exists: true}, 'isValid': ?2, 'partner': ?3}",count = true)
    Long countByBrandIdAndIsValidAndNotAdditionalCashback(String brandId, Instant currTime,
                                                                          Boolean isValid, String partner);

    @Query("{'brandId' :{ $in : ?0}, '$and' :[" +
            "{'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?1}} ]}, " +
            "{'$or':[ {'isValid' : {$exists : false} }, {'isValid': true } ]}], 'partner': ?2}")
    Optional<List<Offer>> findByBrandIds(String[] brandIds, Instant currTime, String partner);

    @Query("{'brandId' : ?0 , 'subvention' : {$exists: true}, 'subvention' : {$gt : 0}, " +
            "'validFrom' : {'$lte': ?1}, 'validTo': {'$gte': ?1}, 'isValid': ?2, 'partner': ?3}")
    Optional<List<Offer>> findByBrandIdWithSubvention(String brandId, Instant time, Boolean isValid, String partner);

    @Query("{'brandId': ?0, 'cardType' : ?1 , '$and' :[" +
            "{'$or':[{'validFrom' : {$exists : false} }, {'validFrom': {'$gte': ?2}}]}, " +
            "{'$or':[{'validTo' : {$exists : false} }, {'validTo': {'$lte': ?2}}]}," +
            "{'$or':[{'isValid' : {$exists : false} }, {'isValid': ?3 }]}," +
            "{'$or':[{'type' : {$exists : false} }, {'type': {$in :?4 }}]}" +
            "], 'partner': ?5}")
    Optional<List<Offer>> findByBrandIdAndCardTypeAndIsValidAndType(String brandId, String cardType, Instant currTime,
                                                                    Boolean isValid, String[] types, String partner);

    @Query("{'brandId': ?0, '$and' :[" +
            "{'$or':[{'validFrom' : {$exists : false} }, {'validFrom': {'$gte': ?1}}]}, " +
            "{'$or':[{'validTo' : {$exists : false} }, {'validTo': {'$lte': ?1}}]}," +
            "{'$or':[{'isValid' : {$exists : false} }, {'isValid': ?2 }]}," +
            "{'$or':[{'type' : {$exists : false} }, {'type': {$in :?3 }}]}" +
            "], 'partner': ?4}")
    Optional<List<Offer>> findByBrandIdAndIsValidAndType(String brandId, Instant currTime, Boolean isValid,
                                                         String[] types, String partner);

}
