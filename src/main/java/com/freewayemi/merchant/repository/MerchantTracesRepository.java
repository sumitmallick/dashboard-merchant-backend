package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantTraces;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantTracesRepository extends MongoRepository<MerchantTraces, String> {
}
