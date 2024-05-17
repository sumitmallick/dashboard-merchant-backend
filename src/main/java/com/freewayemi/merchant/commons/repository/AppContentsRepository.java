package com.freewayemi.merchant.commons.repository;

import com.freewayemi.merchant.commons.entity.AppContents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AppContentsRepository extends MongoRepository<AppContents, String> {
    Optional<List<AppContents>> findAllByContentTypeAndActive(String contentType, Boolean active);

    Optional<List<AppContents>> findAllByContentTypeAndActiveAndUserStatus(String contentType, Boolean active,
                                                                           String userStatus);

    Optional<List<AppContents>> findAllByContentTypeAndActiveAndUserStatusOrUserStatus(String contentType,
                                                                                       Boolean active,
                                                                                       String userStatus,
                                                                                       String userStatus1);

    Optional<AppContents> findByContentTypeAndActive(String contentType, Boolean active);

    Optional<List<AppContents>> findAllByContentTypeAndActiveAndCategory(String contentType, Boolean active,
                                                                         String category);

    Optional<List<AppContents>> findByEntityIdAndActiveAndContentType(String entityId, boolean b, String contentType);

    @Query("{'categories': ?0, 'active' : ?1, 'contentType': ?2, 'expiry': {$gte: ?3}}")
    Optional<List<AppContents>> findByCategoryAndActiveAndContentTypeAndExpiry(String merchantCategory, boolean b, String nudge, Instant from);
}
