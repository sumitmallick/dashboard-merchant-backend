package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BankInterest;
import com.freewayemi.merchant.entity.MerchantVisibilities;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantVisibilityRepository extends MongoRepository<MerchantVisibilities, String> {

    Optional<List<MerchantVisibilities>> findByMerchantIdAndStatus(String merchantId, String status);
}
