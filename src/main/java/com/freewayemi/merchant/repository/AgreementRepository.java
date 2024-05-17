package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.AgreementDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgreementRepository extends MongoRepository<AgreementDetails, String> {
}
