package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BrandsProductRepository extends MongoRepository<BrandProduct, String> {
    @Query("{'brandId' : ?0, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, Instant time, Boolean isValid);

    @Query("{'brandId' : ?0, '$or':[{'product':{ '$regex' : ?1 , $options: 'i'} },{ 'variant':{ '$regex' : ?2 , $options: 'i'}}, {'modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'category':{ $in: ?4 }, 'validFrom': {'$lte': ?5}, validTo: {'$gte': ?5}, 'isValid': ?6}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String searchProduct, String searchVariant, String searchModel,
                                               String[] category, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, '$or':[{'product':{ '$regex' : ?1 , $options: 'i'} },{ 'variant':{ '$regex' : ?2 , $options: 'i'}}, {'modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'validFrom': {'$lte': ?4}, validTo: {'$gte': ?4}, 'isValid': ?5}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String searchProduct, String searchVariant, String searchModel,
                                               Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, 'category':{ $in: ?1 }, 'validFrom': {'$lte': ?2}, validTo: {'$gte': ?2}, 'isValid': ?3}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String[] category, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'modelNo': ?0, 'brandId' : ?1, 'validFrom': {'$lte': ?2}, validTo: {'$gte': ?2}, 'isValid': ?3}")
    Optional<BrandProduct> findByModelNoAndBrandId(String modelNumber, String brandId, Instant time, Boolean isValid);

    @Query("{'brandId': ?0, 'modelNo' : ?1, 'validFrom': {'$lte': ?2}, validTo: {'$gte': ?2}, 'isValid': ?3}")
    Optional<List<BrandProduct>> findByBrandIdAndModelNo(String brandId, String modelNumber, Instant time, Boolean isValid);

    @Query("{'brandId': ?0, 'modelNo' : ?1, 'validFrom': {'$lte': ?2}, validTo: {'$gte': ?2}, 'isValid': ?3}")
    Optional<List<BrandProduct>> findByBrandIdAndModelNo(String brandId, String modelNumber, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId': ?0, 'modelNo' : ?1, 'isPopular' : ?2, 'validFrom': {'$lte': ?3}, validTo: {'$gte': ?3}, 'isValid': ?4}")
    Optional<List<BrandProduct>> findByBrandIdAndModelNo(String brandId, String modelNumber, Boolean isPopular, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, '$or':[{'product':{ '$regex' : ?1 , $options: 'i'} },{ 'variant':{ '$regex' : ?2 , $options: 'i'}}, {'modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'category':{ $in: ?4 }, 'isPopular' : ?5, 'validFrom': {'$lte': ?6}, validTo: {'$gte': ?6}, 'isValid': ?7}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String searchProduct, String searchVariant, String searchModel,
                                               String[] category, Boolean isPopular, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, '$or':[{'product':{ '$regex' : ?1 , $options: 'i'} },{ 'variant':{ '$regex' : ?2 , $options: 'i'}}, {'modelNo' : { '$regex' : ?3 , $options: 'i'}}], 'isPopular' : ?4, 'validFrom': {'$lte': ?5}, validTo: {'$gte': ?5}, 'isValid': ?6}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String searchProduct, String searchVariant, String searchModel, Boolean isPopular,
                                               Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0, 'category':{ $in: ?1 }, 'isPopular' : ?2, 'validFrom': {'$lte': ?3}, validTo: {'$gte': ?3}, 'isValid': ?4 }")
    Optional<List<BrandProduct>> findByBrandId(String brandId, String[] category, Boolean isPopular, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : ?0 , 'isPopular' : ?1, 'validFrom': {'$lte': ?2}, validTo: {'$gte': ?2}, 'isValid': ?3}")
    Optional<List<BrandProduct>> findByBrandId(String brandId, Boolean isPopular, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' : { $in: ?0 }, '$or':[{'category':{ '$regex' : ?1 , $options: 'i' }}, {'product':{ '$regex' : ?2 , $options: 'i'}}, {'variant':{ '$regex' : ?3 , $options: 'i' }}, {'modelNo':{ '$regex' : ?4 , $options: 'i' }}, {'brandId' : { $in: ?5 }}], 'validFrom': {'$lte': ?6}, validTo: {'$gte': ?6}, 'isValid': ?7 }")
    Optional<List<BrandProduct>> findByBrandId(String[] brandIds, String category, String product, String variant, String model, String[] filteredBrandIds, Instant time, Boolean isValid, Pageable pageable);

    @Query("{'brandId' :{ $in: ?0 }, '$or':[{'category':{ '$regex' : ?1 , $options: 'i'}}, {'product':{ '$regex' : ?2 , $options: 'i'}}, {'variant':{ '$regex' : ?3 , $options: 'i' }}, {'modelNo':{ '$regex' : ?4 , $options: 'i' }}, {'brandId' : { $in: ?5 }}], 'isPopular' : ?6, 'validFrom': {'$lte': ?7}, validTo: {'$gte': ?7}, 'isValid': ?8 }")
    Optional<List<BrandProduct>> findByBrandId(String[] brandIds, String category, String product, String variant, String model, String[] filteredBrandIds, Boolean isPopular, Instant time, Boolean isValid,  Pageable pageable);

    @Query("{'_id' : { $in: ?0 }, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<List<BrandProduct>> findByIds(List<String> brandProductIds, Instant time, Boolean isValid);

    @Query("{'_id' : ?0, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<BrandProduct> findByBrandProductId(String brandProductId, Instant time, Boolean isValid);

    @Query("{'skuCode' : ?0, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<BrandProduct> findBySkuCode(String skuCode, Instant time, Boolean isValid);

    @Query("{'modelNo' : ?0, 'validFrom': {'$lte': ?1}, validTo: {'$gte': ?1}, 'isValid': ?2}")
    Optional<BrandProduct> findByModelNo(String modelNo, Instant time, Boolean isValid);
}
