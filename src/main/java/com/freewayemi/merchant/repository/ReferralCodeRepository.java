package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.ReferralCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReferralCodeRepository extends MongoRepository<ReferralCode, String> {
    Optional<ReferralCode> findReferralCodeByReferralCodeAndIsActive(String code, Boolean isActive);
}
