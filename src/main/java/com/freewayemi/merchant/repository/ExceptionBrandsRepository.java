package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.ExceptionBrand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExceptionBrandsRepository extends MongoRepository <ExceptionBrand, String>{

    @Query("{'$or':[ {'brandId': ?0}, {'type': ?1}]}")
    Optional<List<ExceptionBrand>> findByBrandIdOrType(String brandId, String type);

    @Query(value = "{'brandId': ?0}}", count = true)
    Long findBrandIdCount(String brandId);

}
