package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Earning;
import com.freewayemi.merchant.enums.EarningType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantEarningsRepository extends MongoRepository<Earning, String> {

    Optional<List<Earning>> findByMerchantId(String merchantId);

    Optional<List<Earning>> findByMerchantIdAndEarningType(String merchantId, EarningType type);

    Optional<Earning> findByMerchantIdAndReferralMerchantId(String merchantId, String referralMerchantId);

    Optional<Earning> findByScratchCardId(String scratchCardId);

}
