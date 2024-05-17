package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends MongoRepository<Brand, String> {

    @Query("{'_id': { $in: ?0 }}")
    Optional<List<Brand>> findByBrandId(String[] ids);

    @Query("{'_id': { $in: ?0 }, 'name': { '$regex' : ?1 , $options: 'i'}}")
    Optional<List<Brand>> findByBrandId(String[] ids, String brandSearchText);

    Optional<Brand> findByBrandDisplayId(String brandDisplayId);

    @Query("{'name': { $in: ?0 }}")
    Optional<List<Brand>> findByBrand(String[] brands);

    @Query(value = "{}",fields = "{'name': 1, '_id': 0, 'brandId': {'$toString': '$_id'}, 'logo': '$icon'}")
    List<Brand> findByAllBrands();


    @Query("{'category': { $in: ?0 }}")
    Optional<List<Brand>> findByCategory(String[] categories);

    @Query("{'category':{ $in: ?0 }, 'name': { '$regex' : ?1 , $options: 'i'}}")
    Optional<List<Brand>> findByCategory(String[] categories, String brandSearchText);

    @Query("{'name': { $in: ?0 }, 'category': { $in: ?1 }}")
    Optional<List<Brand>> findByBrandAndCategory(String[] brands, String[] categories);

    Optional<Brand> findByIdOrBrandDisplayId(String id, String brandDisplayId);

    @Query("{'category':{ $in: ?0 }, 'brandType' : ?1}")
    Optional<List<Brand>> findByCategoryAndBrandType(String[] categories, String brandType);

    @Query("{'category':{ $in: ?0 }, 'brandType' : ?1, 'name': { '$regex' : ?2 , $options: 'i'}}")
    Optional<List<Brand>> findByCategoryAndBrandType(String[] categories, String brandType, String brandSearchText);

    @Query("{'category':{ $in: ?0 }, 'brandType' : {$ne :?1}}")
    Optional<List<Brand>> findPartnerBrandByCategory(String[] categories, String brandType);

    @Query("{'category':{ $in: ?0 }, 'brandType' : {$ne :?1}, 'name': { '$regex' : ?2 , $options: 'i'}}")
    Optional<List<Brand>> findPartnerBrandByCategory(String[] categories, String brandType, String brandSearchText);

    @Query("{'_id': { $in: ?0 }, '$or':[{'category':{ '$regex' : ?1 , $options: 'i' }}, {'subcategory':{ '$regex' : ?2 , $options: 'i' }}, {'name': { '$regex' : ?3 , $options: 'i'}}] }")
    Optional<List<Brand>> findBrandByCategoryByName(String[] brandIds ,String category, String subCategory, String name);

    @Query("{'_id': { $in: ?0 }, '$or':[{'category':{ '$regex' : ?1 , $options: 'i' }}, {'subcategory':{ '$regex' : ?2 , $options: 'i' }}, {'name': { '$regex' : ?3 , $options: 'i' }}], 'brandType' : ?4 }")
    Optional<List<Brand>> findBrandByCategoryByName(String[] brandIds ,String category, String subCategory, String name, String brandType);

    @Query("{'category': { $in: ?0 }, '$or':[{'category':{ '$regex' : ?1 , $options: 'i' }}, {'subcategory':{ '$regex' : ?2 , $options: 'i' }}, {'name': { '$regex' : ?3 , $options: 'i' }}], 'brandType' : ?4 }")
    Optional<List<Brand>> findPartnerBrandByCategoryByName(String[] merchantBrandCategories, String category, String subCategory, String name, String brandType);

    @Query("{'brandType': {$exists: true}, 'brandType': ?0 }")
    Optional<List<Brand>> findByBrandType(String brandType);

    @Query("{'productCategories':{$exists: true}, 'productCategories': { $in: ?0 }}")
    Optional<List<Brand>> findByProductCategory(String[] productCategories);

    @Query("{'_id': { $in: ?0 }, 'productCategories': {$exists: true}, 'productCategories': { $in: ?1 }}")
    Optional<List<Brand>> findByBrandIdAndProductCategory(String[] brandIds, String[] productCategories);

    Optional<List<Brand>> findByScheduledUnclaim(Boolean scheduledUnclaim);
    @Query("{'hasProducts': ?0}")
    Optional<List<Brand>> findByHasProducts(Boolean hasProducts);

    Optional<Brand> findByBrandCode(String brandCode);

    Optional<Brand> findByName(String name);
}
