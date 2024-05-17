package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Eligibilities;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EligibilitiesRepository extends MongoRepository<Eligibilities, String> {
}
