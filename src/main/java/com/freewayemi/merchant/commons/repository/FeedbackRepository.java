package com.freewayemi.merchant.commons.repository;

import com.freewayemi.merchant.commons.entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Optional<List<Feedback>> findByTransactionId(String transactionId);
}
