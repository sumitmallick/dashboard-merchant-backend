package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantSessionsRepository extends MongoRepository<MerchantSession, String> {
    Optional<List<MerchantSession>> findByMerchantId(String merchantId);

    Optional<List<MerchantSession>> findByMobile(String merchantId);

    Optional<List<MerchantSession>> findByUser(String userId, Pageable pageable);

    Optional<MerchantSession> findByToken(String token);

    Optional<List<MerchantSession>> findByBrandDisplayId(String brandDisplayId);

    Optional<List<MerchantSession>> findByBrandDisplayIdAndInvalid(String brandDisplayId, Boolean invalid);
    Optional<List<MerchantSession>> findByMerchantIdAndInvalid(String merchantId, Boolean invalid);
    Optional<List<MerchantSession>> findByMerchantIdAndMobileAndInvalid(String merchantId, String mobile, Boolean invalid);

    Optional<List<MerchantSession>> findByUserAndInvalid(String userId, Boolean invalid);
}
