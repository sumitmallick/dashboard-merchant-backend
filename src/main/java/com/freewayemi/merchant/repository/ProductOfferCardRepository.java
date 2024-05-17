package com.freewayemi.merchant.repository;


import com.freewayemi.merchant.entity.ProductOfferCard;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ProductOfferCardRepository extends MongoRepository<ProductOfferCard, String> {

    @Query("{'variant.brandId':  { $in: ?0 }, 'isValid': ?1}")
    Optional<List<ProductOfferCard>> findByBrandIdAndIsValid(String[] brandIds, Boolean isValid);

    @Query("{'variant.brandId':  { $in: ?0 }, 'isValid': ?1, '$and' : [{ '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?2 }}]}, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?2 }}]}]}")
    Optional<List<ProductOfferCard>> findByBrandIdAndIsValid(String[] brandIds, Boolean isValid, Instant validOn);

    @Query("{'variant.brandId':  { $in: ?0 }, 'isValid': ?1, '$and' : [{ '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?2 }}]}, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?2 }}]}], 'partner': ?3 }")
    Optional<List<ProductOfferCard>> findByBrandIdAndIsValidAndPartner(String[] brandIds, Boolean isValid, Instant validOn, String partner);

    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?1 } , 'isValid': ?2,  '$and' : [ { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]} , { '$or':[{'preApprovedCard' : { $in: ?4 }}, {'creditCard' : { $in: ?5 }}] }]}")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] categories, Boolean isValid, Instant validOn, String[] debit,
                                                                               String[] credit, Pageable pageable);

    @Query("{'variant.brandId': { $in: ?0 } , 'variant.category' : { $in: ?1 } , 'isValid': ?2,   '$and' : [ { '$or':[{'preApprovedCard' : { $in: ?3 }}, {'creditCard' : { $in: ?4 }}] },   {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]} , {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?5 }}]} ] }")
    Optional<List<ProductOfferCard>> findByBrandIdAndCategoryAndIsValidAndBanks(String[] brandIds, String[] categories, Boolean isValid, String[] debit, String[] credit,  Instant validOn);

    @Query("{'variant.brandId': { $in: ?0 } , 'isValid': ?1,   '$and' : [{    '$or':[{'preApprovedCard' : { $in: ?2 }}, {'creditCard' : { $in: ?3 }}]  },  { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?4 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?4 }}]} ] }")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndIsValid(String[] brandIds, Boolean isValid, String[] debit, String[] credit, Instant validOn);

    @Query("{'variant.brandId': { $in: ?0 } , 'variant.category' : { $in: ?1 } , 'isValid': ?2, '$and' :[{ '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}] }, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]}] }")
    Optional<List<ProductOfferCard>> findByBrandIdAndCategoryAndIsValid(String[] brandIds, String[] categories, Boolean isValid, Instant validOn);


    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?1 } , 'isValid': ?2,  '$and' : [ { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]} , { '$or':[{'preApprovedCard' : { $in: ?4 }}, {'creditCard' : { $in: ?5 }}] }]}")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndCategoryAndIsValid(String[] brandIds, String[] categories, Boolean isValid, Instant validOn, String[] debit,
                                                                               String[] credit);

    @Query("{'variant.brandId':  { $in: ?0 }, 'isValid': ?1, '$and' : [{'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?2 }}]}, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?2 }}]} ]  , 'type' : {$exist:true}, 'type':  { $in: ?3 }, 'partner': ?4}")
    Optional<List<ProductOfferCard>> findByBrandIdAndIsValidAndTypeAndPartner(String[] brandIds, Boolean isValid, Instant validOn , String[] types, String partner);

    @Query("{'variant.brandId': { $in: ?0 } , 'variant.category' : { $in: ?1 } , 'isValid': ?2,   '$and' : [ { '$or':[{'preApprovedCard' : { $in: ?3 }}, {'creditCard' : { $in: ?4 }}] },   {'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?5 }}]}, {'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?5 }}]}  ], 'type' : {$exist:true}, 'type':  { $in: ?6 }  }")
    Optional<List<ProductOfferCard>> findByBrandIdAndCategoryAndIsValidAndBanksAndTypes(String[] brandIds, String[] categories, Boolean isValid, String[] debit, String[] credit,  Instant validOn, String[] types);

    @Query("{'variant.brandId': { $in: ?0 } , 'variant.category' : { $in: ?1 } , 'isValid': ?2, '$and' : [{'$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}]} ,{'$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]} ], 'type' : {$exist:true}, 'type':  { $in: ?4 } }")
    Optional<List<ProductOfferCard>> findByBrandIdAndCategoryAndIsValidAndType(String[] brandIds, String[] categories, Boolean isValid, Instant validOn, String[] types);

    @Query("{'variant.brandId': { $in: ?0 } , 'isValid': ?1,   '$and' : [{    '$or':[{'preApprovedCard' : { $in: ?2 }}, {'creditCard' : { $in: ?3 }}]  },  { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?4 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?4 }}]} ], 'type' : {$exist:true}, 'type':  { $in: ?5 } }")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndIsValidAndTypes(String[] brandIds, Boolean isValid, String[] debit, String[] credit, Instant validOn, String[] types);



    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?1 } , 'isValid': ?2,  '$and' : [ { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]} , { '$or':[{'preApprovedCard' : { $in: ?4 }}, {'creditCard' : { $in: ?5 }}] }], 'type': { $in: ?6 }}")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndCategoryAndIsValidAndType(String[] brandIds, String[] categories, Boolean isValid, Instant validOn, String[] debit,
                                                                               String[] credit, String[] types);

    @Query("{'variant.brandId': { $in: ?0 }, 'variant.category' : { $in: ?1 } , 'isValid': ?2,  '$and' : [ { '$or':[ {'validFrom' : {$exists : false} }, {'validFrom': {'$lte': ?3 }}]}, { '$or':[ {'validTo' : {$exists : false} }, {'validTo': {'$gte': ?3 }}]} , { '$or':[{'preApprovedCard' : { $in: ?4 }}, {'creditCard' : { $in: ?5 }}] }], 'type': { $in: ?6 }}")
    Optional<List<ProductOfferCard>> findByBrandIdAndBankAndCategoryAndIsValidAndType(String[] brandIds, String[] categories, Boolean isValid, Instant validOn, String[] debit,
                                                                                      String[] credit, String[] types ,Pageable pageable);

    @Query("{'_id':  ?0, 'isValid': ?1}")
    Optional<ProductOfferCard> findById(ObjectId productOfferCardId, Boolean isValid);
}
