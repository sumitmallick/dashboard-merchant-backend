package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandProductMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BrandProductMetaDataRepository extends MongoRepository<BrandProductMetaData,String> {

    Optional<List<BrandProductMetaData>> findByKeyAndIsValid(String key, Boolean isValid);
}
