package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantGSTDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantGstAuthInfoRepository extends MongoRepository<MerchantGSTDetails, String> {
    Optional<List<MerchantGSTDetails>> findByMerchantId(String merchantId, Pageable pageable);
}
