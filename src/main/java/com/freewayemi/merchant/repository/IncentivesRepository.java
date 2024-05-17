package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Item;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Objects;
import java.util.Optional;

public interface IncentivesRepository extends MongoRepository<Item,String> {
    Optional<Item> findById(String incentiveId);

}
