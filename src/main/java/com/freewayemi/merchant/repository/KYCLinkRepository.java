package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.KYCLink;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface KYCLinkRepository extends MongoRepository<KYCLink, String> {
    Optional<List<KYCLink>> findByMerchantId(String merchantId, Pageable pageable);
}
