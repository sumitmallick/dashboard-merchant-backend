package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification,String> {

    Optional<List<Notification>> findByLeadOwnerId(String leadOwnerId);

    @Query(value = "{ 'source': ?0, 'merchantId': ?1, 'readStatus': ?2, 'active': true }", exists = true)
    boolean existsBySourceAndMerchantIdAndReadStatus(String source, String merchantId, Boolean readStatus);

    @Query(value = "{ 'source': ?0, 'merchantId': ?1, 'groups': ?2, 'readStatus': ?3, 'active': true }", exists = true)
    boolean existsBySourceAndMerchantIdAndGroupAndReadStatus(String source, String merchantId, String group,
                                                             Boolean readStatus);
    @Query("{ 'source': ?0, 'merchantId': ?1, 'active': true }")
    Slice<Notification> findBySourceAndMerchantId(String source, String merchantId, Pageable pageable);

    @Query("{ 'source': ?0, 'merchantId': ?1, 'eventName': ?2, 'active': true }")
    Slice<Notification> findBySourceAndMerchantIdAndEventName(String source, String merchantId, String eventName,
                                                              Pageable pageable);

    @Query("{ 'source': ?0, 'merchantId': ?1, 'groups': ?2, 'active': true }")
    Slice<Notification> findBySourceAndMerchantIdAndGroup(String source, String merchantId, String group,
                                                          Pageable pageable);

    @Query("{ 'source': ?0, 'merchantId': ?1, 'groups': ?2, 'eventName': ?3, 'active': true }")
    Slice<Notification> findBySourceAndMerchantIdAndGroupAndEventName(String source, String merchantId, String group,
                                                                      String eventName, Pageable pageable);

    // sales notifications
    @Query(value = "{ 'source': ?0, 'leadOwnerId': ?1, 'readStatus': ?2, 'active': true }", exists = true)
    boolean existsBySourceAndLeadOwnerIdAndReadStatus(String source, String leadOwnerId, Boolean readStatus);

    @Query(value = "{ 'source': ?0, 'leadOwnerId': ?1, 'groups': ?2, 'readStatus': ?3, 'active': true }", exists = true)
    boolean existsBySourceAndLeadOwnerIdAndGroupAndReadStatus(String source, String leadOwnerId, String group,
                                                              Boolean readStatus);

    @Query("{ 'source': ?0, 'leadOwnerId': ?1, 'active': true }")
    Slice<Notification> findBySourceAndLeadOwnerId(String source, String leadOwnerId, Pageable pageable);

    @Query("{ 'source': ?0, 'leadOwnerId': ?1, 'eventName': ?2, 'active': true }")
    Slice<Notification> findBySourceAndLeadOwnerIdAndEventName(String source, String leadOwnerId, String eventName,
                                                               Pageable pageable);

    @Query("{ 'source': ?0, 'leadOwnerId': ?1, 'groups': ?2, 'active': true }")
    Slice<Notification> findBySourceAndLeadOwnerIdAndGroup(String source, String leadOwnerId, String group,
                                                           Pageable pageable);

    @Query("{ 'source': ?0, 'leadOwnerId': ?1, 'groups': ?2, 'eventName': ?3, 'active': true }")
    Slice<Notification> findBySourceAndLeadOwnerIdAndGroupAndEventName(String source, String leadOwnerId, String group,
                                                                       String eventName, Pageable pageable);
}
