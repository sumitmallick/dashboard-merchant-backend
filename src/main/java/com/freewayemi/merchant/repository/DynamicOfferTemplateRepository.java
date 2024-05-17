package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.DynamicOfferTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DynamicOfferTemplateRepository extends MongoRepository<DynamicOfferTemplate, String> {
    Optional<DynamicOfferTemplate> findByName(String name);
}
