package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.SalesAgent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SalesAgentRepository extends MongoRepository<SalesAgent, String> {
    Optional<SalesAgent> findByMobile(String mobile);
}
