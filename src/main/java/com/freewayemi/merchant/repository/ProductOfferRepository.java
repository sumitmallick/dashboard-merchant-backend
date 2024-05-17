package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.ProductOffer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProductOfferRepository extends MongoRepository<ProductOffer, String> {

    @Query("{'variant.brandId': { $in: ?0 }, 'bankCodes' : { $in: ?1 } , 'variant.category' : { $in: ?2 } , " +
            "'isValid': ?3, '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?4}} ]}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] banks,
                                                                           String[] categories, Boolean isValid, Instant validOn, Pageable pageable);

    @Query("{'variant.brandId': { $in: ?0 }, 'bankCodes' : { $in: ?1 } , 'variant.category' : { $in: ?2 } , " +
            "'isValid': ?3, '$and' : [{ '$or':[ {'variant.productName' : { '$regex' : ?4 , $options: 'i'}}, " +
            "{'variant.name' : { '$regex' : ?5 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?6 , $options: 'i'}}] } " +
            ", {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?7}}] }]}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValidAndProduct(String[] brandIds, String[] banks,
                                                                                     String[] categories, Boolean isValid, String product, String variant, String modelNumber, Instant validOn, Pageable pageable);

    @Query("{'variant.brandId': ?0, 'isValid': ?1}")
    Optional<List<ProductOffer>> findByBrandIdAndIsValid(String brandId, Boolean isValid);

    @Query("{'isValid': ?0}")
    Optional<List<ProductOffer>> findByIsValid(Boolean isValid);

    @Query("{'variant.brandId':  { $in: ?0 }, 'isValid': ?1}")
    Optional<List<ProductOffer>> findByBrandIdAndIsValid(String[] brandIds, Boolean isValid);

    @Query("{'variant.brandId': { $in: ?0 }, 'bankCodes' : { $in: ?1 } , 'variant.category' : { $in: ?2 } , " +
            "'isValid': ?3}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] banks,
                                                                           String[] categories, Boolean isValid);

    @Query("{'variant.brandId': { $in: ?0 }, 'bankCodes' : { $in: ?1 } , 'isValid': ?2}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndIsValid(String[] brandIds, String[] banks, Boolean isValid);

    @Query("{'variant.brandId': { $in: ?0 } , 'variant.category' : { $in: ?1 } , 'isValid': ?2}")
    Optional<List<ProductOffer>> findByBrandIdAndCategoryAndIsValid(String[] brandIds, String[] categories, Boolean isValid);


    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 } , 'isValid': ?4" +
            " , '$and' : [ {'$or':[ {'variant.productName' : { '$regex' : ?5 , $options: 'i'}}, " +
            "{'variant.name' : { '$regex' : ?6 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?7 , " +
            "$options: 'i'}}, {'variant.brandName' : { '$regex' : ?9 , $options: 'i'}} ] }, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?8 }}]}, " +
            "{'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?8 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}]}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValidAndProduct(String[] brandIds, String[] credit, String[] debit,
                                                                                     String[] categories, Boolean isValid, String product, String variant, String modelNumber, Instant validOn, String brandName, Pageable pageable);


    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 }, 'isValid': ?4, " +
            " '$and' : [{'$or':[ {'validTo' : { $exists : false}}, {'validTo': {'$gte': ?5}} ]} ," +
            " {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}] }")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] credit, String[] debit,
                                                                           String[] categories, Boolean isValid, Instant validOn);

    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 }, 'isValid': ?4, " +
            " '$and' : [{'$or':[ {'validTo' : { $exists : false}}, {'validTo': {'$gte': ?5}} ]} ," +
            " {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}] }")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] credit, String[] debit,
                                                                           String[] categories, Boolean isValid, Instant validOn, Pageable pageable);


    @Query("{'productOfferCardId': ?0,  'variant.category' : { $in: ?1 }, '$or':[{'creditCard' : { $in: ?2 }}, {'preApprovedCard' : { $in: ?3 }}]}]}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String[] categories, String[] credit, String[] debit, String segmentId);

    @Query("{'productOfferCardId': ?0,  'variant.category' : { $in: ?1 }, '$or':[{'creditCard' : { $in: ?2 }}, {'preApprovedCard' : { $in: ?3 }}]}]}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String[] categories, String[] credit, String[] debit, String segmentId, Pageable pageable);


    @Query("{'productOfferCardId': ?0}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId);

    @Query("{'productOfferCardId': ?0, '$or':[ {'variant.productName' : { '$regex' : ?1 , $options: 'i'}}, {'variant.name' : { '$regex' : ?2 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?3 , $options: 'i'}}]}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String product, String variant, String modelNumber, String segmentId, Pageable pageable);


    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 } , 'isValid': ?4" +
            " , '$and' : [ {'$or':[ {'variant.productName' : { '$regex' : ?5 , $options: 'i'}}, " +
            "{'variant.name' : { '$regex' : ?6 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?7 , " +
            "$options: 'i'}}, {'variant.brandName' : { '$regex' : ?10 , $options: 'i'}}    ] }, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?8 }}]}, " +
            "{'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?8 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}], 'type': { $in: ?9 }}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValidAndProductAndType(String[] brandIds, String[] credit, String[] debit,
                                                                                            String[] categories, Boolean isValid, String product, String variant, String modelNumber, Instant validOn, String[] types, String brandName, Pageable pageable);

    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 }, 'isValid': ?4, " +
            " '$and' : [{'$or':[ {'validTo' : { $exists : false}}, {'validTo': {'$gte': ?5}} ]} ," +
            " {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}] , 'type': { $in: ?6 }}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValidAndType(String[] brandIds, String[] credit, String[] debit,
                                                                                  String[] categories, Boolean isValid, Instant validOn, String[] types);


    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?3 }, 'isValid': ?4, " +
            " '$and' : [{'$or':[ {'validTo' : { $exists : false}}, {'validTo': {'$gte': ?5}} ]} ," +
            " {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]} , " +
            "{'$or':[{'creditCard' : { $in: ?1 }}, {'preApprovedCard' : { $in: ?2 }}]}] , 'type': { $in: ?6 }}")
    Optional<List<ProductOffer>> findByBrandIdAndBankAndCategoryAndIsValidAndType(String[] brandIds, String[] credit, String[] debit,
                                                                                  String[] categories, Boolean isValid, Instant validOn, String[] types, Pageable pageable);


    @Query("{'productOfferCardId': ?0, '$or':[ {'variant.productName' : { '$regex' : ?1 , $options: 'i'}}, {'variant.name' : { '$regex' : ?2 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'variant.category' : { $in: ?4 }, '$or':[{'creditCard' : { $in: ?5 }}, {'preApprovedCard' : { $in: ?6 }}]}]}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String product, String variant, String modelNumber,  String[] categories, String[] credit, String[] debit, String segmentId);

    @Query("{'productOfferCardId': ?0, '$or':[ {'variant.productName' : { '$regex' : ?1 , $options: 'i'}}, {'variant.name' : { '$regex' : ?2 , $options: 'i'}}, {'variant.modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'variant.category' : { $in: ?4 }, '$or':[{'creditCard' : { $in: ?5 }}, {'preApprovedCard' : { $in: ?6 }}]}]}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String product, String variant, String modelNumber,  String[] categories, String[] credit, String[] debit, String segmentId, Pageable pageable);


    @Query("{'productOfferCardId': ?0, 'variant.category' : { $in: ?1 }}")
    Optional<List<ProductOffer>> findByProductOfferCardId(String productOfferCardId, String[] categories, Pageable pageable);

    @Query("{'segmentId': { $in: ?0 }, 'isValid' : ?1 }")
    Optional<List<ProductOffer>> findBySegmentIdAndValid(String[] segmentIds, Boolean isValid);

    @Query("{'productOfferCardId': ?0, 'isValid' : ?1 }")
    Optional<List<ProductOffer>> findByProductOfferCardIdAndIsValid(String productOfferCardId, Boolean isValid);

}
