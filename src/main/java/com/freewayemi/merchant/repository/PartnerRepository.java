package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantGSTDetails;
import com.freewayemi.merchant.entity.Partner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PartnerRepository extends MongoRepository<Partner, String> {
    Optional<Partner> findByName(String name);
}
