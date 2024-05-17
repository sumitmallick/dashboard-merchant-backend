package com.freewayemi.merchant.commons.repository;

import com.freewayemi.merchant.commons.entity.AuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AuthUserRepository extends MongoRepository<AuthUser, String> {
    Optional<AuthUser> findTopByUserIdOrderByExpiryDesc(String userId);
    Optional<AuthUser> findTopByUserIdAndUserTypeOrderByExpiryDesc(String urserId, String type);
    Optional<List<AuthUser>> findByUserId(String userId);

}
