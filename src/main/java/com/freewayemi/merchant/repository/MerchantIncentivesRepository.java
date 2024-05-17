package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantIncentive;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.*;

public interface MerchantIncentivesRepository extends MongoRepository<MerchantIncentive,String> {
     Optional<List<MerchantIncentive>> findByMerchantId(String merchantId);

     @Query(value = "{'merchantId': ?0 }", fields = "{'_id': 0, 'lastModifiedDate': 0}")
     Optional<List<MerchantIncentive>> findByMerchantIdAndIdAndLastModifiedDate(String merchantId);
}
