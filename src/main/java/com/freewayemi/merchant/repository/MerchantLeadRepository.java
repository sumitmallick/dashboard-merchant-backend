package com.freewayemi.merchant.repository;


import com.freewayemi.merchant.entity.MerchantLead;
import com.freewayemi.merchant.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MerchantLeadRepository extends MongoRepository<MerchantLead, String> {
    Optional<List<MerchantLead>> findByLeadOwnerIds(String leadOwnerId);

    Optional<MerchantLead> findByMobileAndPartner(String mobile, String partner);

    @Query(value = "{'leadOwnerIds': ?0, 'createdDate': {$gt:?1, $lt: ?2}}", count = true)
    long findByLeadOwnerIdAndCreatedDate(String leadOwnerId, Instant fromDate , Instant endDate);

    @Query(value = "{'leadOwnerIds': ?0, 'createdDate': {$gt:?1, $lt: ?2}, 'isOnboarded': ?3}", count = true)
    long findByLeadOwnerIdAndApprovedDateAndOnBoardedTilMtd(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnboarded);

    @Query(value = "{'leadOwnerIds': ?0 , 'createdDate': {$gt:?1, $lt: ?2}, 'isActivated': ?3 }", count = true)
    long findByLeadOwnerIdAndApprovedDateAndActivatedTilMtd(String leadOwnerId, Instant fromDate,Instant endDate,
                                                            Boolean isActivated);

    @Query(value = "{'leadOwnerIds': ?0 , 'createdDate': {$gt:?1, $lt: ?2}}", count = true)
    long findByLeadOwnerIdAndCreatedDateTillMtd(String leadOwnerId, Instant fromDate, Instant endDate);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': ?1}", count = true)
    long findByLeadOwnerIdAndStatusLeadsCount(String leadOwnerId, String status);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': {$nin : ?1}}", count = true)
    long findByLeadOwnerIdAndOnBoardingLeadsCount(String leadOwnerId, List<String> statuses);

    @Query(value = "{'leadOwnerIds': ?0}", count = true)
    long findAllLeadsByLeadOwnerId(String leadOwnerId);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': {$in : ?1}, '$or':[ {'gstData.businessName':  { '$regex' : ?2 , $options: 'i'}}, {'displayId':  { '$regex' : ?2 , $options: 'i'}} ]}")
    Optional<List<MerchantLead>> findByStatusAndBussinessName(String leadOwnerId, List<Status> status, String name);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': {$in : ?1}}")
    Optional<List<MerchantLead>> findByLeadOwnerIdsAndStatus(String leadOwnerId, List<Status> status);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': {$nin : ?1}, '$or':[ {'gstData.businessName':  { '$regex' : ?2 , $options: 'i'}}, {'displayId':  { '$regex' : ?2 , $options: 'i'}} ]}")
    Optional<List<MerchantLead>> findByStatusAndBussinessNameForOnBoarding(String leadOwnerId, List<Status> status, String name);

    @Query(value = "{'leadOwnerIds': ?0 , 'status': {$nin : ?1}}")
    Optional<List<MerchantLead>> findByLeadOwnerIdsAndStatusOnBoarding(String leadOwnerId, List<Status> status);

    @Query(value = "{'leadOwnerIds': ?0 , '$or':[ {'gstData.businessName':  { '$regex' : ?1 , $options: 'i'}}, {'displayId':  { '$regex' : ?1 , $options: 'i'}} ]}}")
    Optional<List<MerchantLead>> findByLeadOwnerIdsAndBussinessName(String leadOwnerId, String name);

    Optional<MerchantLead> findByDisplayId(String displayId);
}