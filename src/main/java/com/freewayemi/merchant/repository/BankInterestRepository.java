package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BankInterest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BankInterestRepository extends MongoRepository<BankInterest, String> {
    Optional<BankInterest> findByBrandId(String brandDisplayId);

    Optional<BankInterest> findByMerchantId(String merchantDisplayId);
}
