package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.SalesAgentWinner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SalesAgentWinnerRepository extends MongoRepository<SalesAgentWinner, String> {
    Optional<SalesAgentWinner> findByEmailAndActive(String email, Boolean active);
}
