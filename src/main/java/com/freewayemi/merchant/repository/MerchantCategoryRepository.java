package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantCategoryRepository extends MongoRepository<MerchantCategory, String> {
    Optional<List<MerchantCategory>>  findByVersionOrderByOrderAsc(String version);
}
