package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MerchantUserRepository extends MongoRepository<MerchantUser, String> {

    Optional<MerchantUser> findByMobileAndIsDeleted(String mobile, Boolean isDeleted);

    Optional<MerchantUser> findByEmailAndIsDeleted(String email, Boolean isDeleted);

    Optional<MerchantUser> findByDisplayId(String displayId);

    Optional<MerchantUser> findBySmsCode(String displayId);

    Optional<MerchantUser> findByQrCode(String qr);

    Optional<MerchantUser> findByQrCodeOrSoftQrCodeOrStoreCode(String qrCode, String softQrCode, String storeCode);

    Optional<MerchantUser> findByStoreCode(String qr);

    Optional<MerchantUser> findByReferralCode(String referralId);

    Optional<List<MerchantUser>> findByStatus(String status);

    @Query("{'category': { $in: ?0 }, 'status': ?1}")
    Optional<List<MerchantUser>> findByCategoryAndStatus(String[] category, String status);

    @Query("{'status': ?0, 'category': { $in: ?1 }, 'params.brand': ?2, 'address.city': ?3}")
    Optional<List<MerchantUser>> findByStatusAndCategoryAndBrandAndCity(String status, String[] category, String brand, String city);

    @Query("{'category': { $in: ?0 }, 'params.brand': ?1, 'status': ?2}")
    Optional<List<MerchantUser>> findByCategoryAndBrandAndStatus(String[] category, String brand, String status);

    @Query("{'category': { $in: ?0 }, 'address.city': ?1, 'status': ?2}")
    Optional<List<MerchantUser>> findByCategoryAndCityAndStatus(String[] category, String city, String status);

    @Query("{'params.brand': ?0, 'address.city': ?1, 'status': ?2}")
    Optional<List<MerchantUser>> findByBrandAndCityAndStatus(String brand, String city, String status);

    @Query("{'params.brand': ?0, 'status': ?1}")
    Optional<List<MerchantUser>> findByBrandAndStatus(String brand, String status);

    @Query("{'address.city': ?0, 'status': ?1 }")
    Optional<List<MerchantUser>> findByCityAndStatus(String city, String status);

    @Query("{'params.externalStoreCode': ?0 }")
    Optional<MerchantUser> findByExternalStoreCode(String storeCode);

    @Query("{'params.externalStoreCode': ?0, 'status': ?1  }")
    Optional<MerchantUser> findByExternalStoreCodeAndStatus(String storeCode, String status);


    @Query("{'$or':[ {'params.brandId':?0 },{ 'params.brandIds':?1} ] ,'address.city': { '$regex' : ?2 , $options: " + "'i'}, 'status': ?3 }")
    Optional<List<MerchantUser>> findByBrandIdAndBrandIdsAndCityAndStatus(String brandId, String brandIds, String city, String status, Pageable pageable);

    @Query("{'$or':[ {'params.brandId':?0 },{ 'params.brandIds':?1} ] ,'address.city': { '$regex' : ?2 , $options: " +
            "'i'}, 'status': ?3 }")
    Optional<List<MerchantUser>> findByBrandIdAndBrandIdsAndCityAndStatus(String brandId, String brandIds, String city,
                                                                          String status);

    @Query("{'$or':[ {'params.brandId':?0 },{ 'params.brandIds':?1} ] , 'status': ?2 }")
    Optional<List<MerchantUser>> findByBrandIdAndStatus(String brand, String brandIds, String status);

    @Query("{'$or':[ {'params.brandId':?0 },{ 'params.brandIds':?1} ] , 'status': ?2 }")
    Optional<List<MerchantUser>> findByBrandIdAndStatus(String brand, String brandIds, String status,
                                                        Pageable pageable);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?4 }}, 'status': ?2 , 'shopName': { '$regex' : ?3 , $options: 'i'} }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndShopName(Double latitude, Double longitude, String status, String shopName, Double maxDistance, Pageable pageable);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?4 }}, 'status': ?2 , 'shopName': { '$regex' : ?3 , $options: 'i'} }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndShopName(Double latitude, Double longitude, String status, String shopName, Double maxDistance);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?3  }}, 'status': ?2 }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinates(Double latitude, Double longitude, String status, Double maxDistance, Pageable pageable);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?3  }}, 'status': ?2 }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinates(Double latitude, Double longitude, String status, Double maxDistance);

    @Query("{ 'status': ?0 , 'shopName': { '$regex' : ?1 , $options: 'i'} }")
    Optional<List<MerchantUser>> findByStatusAndShopName(String status, String shopName, Pageable pageable);

    @Query("{ 'status': ?0 , 'shopName': { '$regex' : ?1 , $options: 'i'} }")
    Optional<List<MerchantUser>> findByStatusAndShopName(String status, String shopName);

    @Query("{'status': ?0 }")
    Optional<List<MerchantUser>> findByStatus(String status, Pageable pageable);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?5 }}, 'status': ?2 , '$or':[ {'params.brandId':{ $in: ?3 } },{ 'params.brandIds':{ $in: ?4 }} ] }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndBrands(Double latitude, Double longitude, String status, String[] brands, String[] brandIds, Double maxDistance, Pageable pageable);

    @Query("{ 'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] } , $maxDistance: ?5  }}, 'status': ?2 , '$or':[ {'params.brandId':{ $in: ?3 } },{ 'params.brandIds':{ $in: ?4 }} ] }")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndBrands(Double latitude, Double longitude, String status, String[] brands, String[] brandIds, Double maxDistance);

    @Query("{ 'status': ?0 , '$or':[ {'params.brandId':{ $in: ?1 } },{ 'params.brandIds':{ $in: ?2 }} ] }")
    Optional<List<MerchantUser>> findByStatusAndBrands(String status, String[] brands, String[] brandIds, Pageable pageable);

    @Query("{ 'status': ?0 , '$or':[ {'params.brandId':{ $in: ?1 } },{ 'params.brandIds':{ $in: ?2 }} ] }")
    Optional<List<MerchantUser>> findByStatusAndBrands(String status, String[] brands, String[] brandIds);

    @Query("{'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] }, $maxDistance: ?6 }}, 'status': ?2 , 'shopName': { '$regex' : ?3 , $options: 'i'}, '$or':[ {'params.brandId':{ $in: ?4 }},{'params.brandIds':{$in: ?5 }}]}")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndShopNameAndBrands(Double latitude, Double longitude, String status, String shopName, String[] brands, String[] brandIds, Double maxDistance, Pageable pageable);

    @Query("{'address.reverseCoordinates': {$exists: true}, 'address.reverseCoordinates' : { $near : { $geometry: { type: 'Point',  coordinates: [ ?1, ?0 ] } , $maxDistance: ?6}}, 'status': ?2 , 'shopName': { '$regex' : ?3 , $options: 'i'}, '$or':[ {'params.brandId':{ $in: ?4 }},{'params.brandIds':{$in: ?5 }}]}")
    Optional<List<MerchantUser>> findByStatusAndAddressCoordinatesAndShopNameAndBrands(Double latitude, Double longitude, String status, String shopName, String[] brands, String[] brandIds, Double maxDistance);

    @Query("{ 'status': ?0 , 'shopName': { '$regex' : ?1 , $options: 'i'}, '$or':[ {'params.brandId':{ $in: ?2 }}, {'params.brandIds':{$in: ?3 }}] }")
    Optional<List<MerchantUser>> findByStatusAndShopNameAndBrands(String status, String shopName, String[] brands, String[] brandIds, Pageable pageable);

    @Query("{ 'status': ?0 , 'shopName': { '$regex' : ?1 , $options: 'i'},'$or':[ {'params.brandId':{ $in: ?2 }}, {'params.brandIds':{$in: ?3 }}] }")
    Optional<List<MerchantUser>> findByStatusAndShopNameAndBrands(String status, String shopName, String[] brands, String[] brandIds);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'createdDate': { $gt:?1 , $lt:?2 }}",
            count = true)
    long findByLeadOwnerIdAndCreatedDateTillMtd(String leadOwnerId, Instant fromDate, Instant endDate);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'createdDate':{ $gt:?1 , $lt:?2}}", count = true)
    long findByLeadOwnerIdAndCreatedDate(String leadOwnerId, Instant fromDate, Instant endDate);

    @Query("{'$or': [{'params.leadOwnerId': ?0}, {'params.leadOwnerIds': ?0}]}")
    Optional<List<MerchantUser>> findByLeadOwnerId(String leadOwnerId, Pageable pageable);

    @Query("{'$or': [{'params.leadId': ?0}, {'params.leadOwnerIds': ?0}, {'params.leadOwnerId': ?0}]}")
    Optional<List<MerchantUser>> findByLeadId(String leadOwnerId, Pageable pageable);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0}, {'params.leadOwnerIds': ?0}], 'createdDate':{'$gt': ?1, '$lt': ?2}, 'isOnboarded':  ?3}", count = true)
    long findMerchantOnBoardedCount(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnBoarded);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0}, {'params.leadOwnerIds': ?0}], 'status': ?1, 'isOnboarded':  {'$ne': ?2}}", count = true)
    long findOnBoardingMerchantsCount(String leadOwnerId, String status, Boolean isOnBoarded);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0}, {'params.leadOwnerIds': ?0}], 'isOnboarded': ?1, 'isActivated': {'$ne': ?2}}", count = true)
    long findMerchantActivationCounts(String leadOwnerId, Boolean isOnBoarded, Boolean isActivated);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'isActivated': true ,'createdDate': {$gt:?1, $lt: ?2}}", count = true)
    long findMerchantActivatedCountTillMTD(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isActivated);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'isOnboarded': true, 'createdDate':{$gt:?1, $lt: ?2}}", count = true)
    long findMerchantOnBoardedCountTillMTD(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnboarded);

    @Query(value = "{'$or': [{'params.leadOwnerId': ?0}, {'params.leadOwnerIds': ?0}], 'status': ?1,  'isOnboarded':  ?2}", count = true)
    long findMerchantsOnBoardedCountTillNow(String leadOwnerId, String status, Boolean isOnBoarded);

    Optional<List<MerchantUser>> findByMobile(String mobile, Pageable pageable);

    Optional<List<MerchantUser>> findByGst(String gst, Pageable pageable);

    @Query(value = "{'_id': ?0, 'status': ?1, 'qrActivationDate': {'$exists':  ?2}, 'deviceToken':  {'$exists': ?3}}", count = true)
    long findMerchantsCountNotActivated(String merchantId, String status, Boolean isQrActivationDate, Boolean isDeviceToken);

    @Query("{'$or': [{'_id': {'$in': ?0}}, {'mobile':  {'$in': ?0}}, {'displayId':  {'$in': ?0}}]}")
    Optional<List<MerchantUser>> findByIdsOrDisplayIdOrMobile(String[] ids);

    Optional<List<MerchantUser>> findByIdAndIncentiveIds(Object _id, String incentiveIds);

    @Query("{'_id': {'$in': ?0}, 'status': ?1, 'isOnboarded': ?2}")
    Optional<List<MerchantUser>> findByIdAndOnBoarded(List<String> ids, String status, Boolean isOnboarded);

    @Query("{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}")
    Optional<List<MerchantUser>> findByLeadOwnerIds(String leadOwnerId, Pageable pageable);

    Optional<MerchantUser> findByMobileAndPartner(String mobile, String partner);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1}", count = true)
    long findByLeadOwnerIdAndStatusLeadsCount(String leadOwnerId, String status);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1, 'isOnboarded': {$ne: ?2}}", count = true)
    long findByLeadOwnerIdAndOnBoardingLeadsCount(String leadOwnerId, String status, Boolean isOnboarded);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}", count = true)
    long findAllLeadsByLeadOwnerId(String leadOwnerId);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1, '$or':[ {'gstData.businessName': ?2}, {'displayId': ?2 } ]}")
    Optional<List<MerchantUser>> findByStatusAndBussinessName(String leadOwnerId, String status, String name, Pageable pageable);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1}")
    Optional<List<MerchantUser>> findByLeadOwnerIdsAndStatus(String leadOwnerId, String status, Pageable pageable);

    @Query(value = "{'$and' : [{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}, {'$or':[ {'gstData.businessName':  ?2 }, {'displayId': ?2} ]}],'status':  ?1, 'isOnboarded': {$ne:?3}}")
    Optional<List<MerchantUser>> findByStatusAndBussinessNameForOnBoarding(String leadOwnerId, String status, String name, Boolean isOnboarded, Pageable pageable);
    @Query(value = "{'$and' : [{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}, {'$or':[ {'gstData.businessName':  ?2}, {'displayId': ?2} ]}],'status': ?1, 'isOnboarded':?3}")
    Optional<List<MerchantUser>> findByStatusAndBussinessNameForApproved(String leadOwnerId, String status, String name, Boolean isOnboarded, Pageable pageable);
    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1, 'isOnboarded': {$ne:?2}}")
    Optional<List<MerchantUser>> findByLeadOwnerIdsAndStatusOnBoarding(String leadOwnerId, String status, Boolean isOnboarded);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }] , 'status': ?1, 'isOnboarded': ?2}")
    Optional<List<MerchantUser>> findByLeadOwnerIdsAndStatusApproved(String leadOwnerId, String status, Boolean isOnboarded, Pageable pageable);

    @Query(value = "{'$and' : [{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}, {'$or':[ {'gstData.businessName': ?1 }, {'displayId': ?1} ]}]}")
    Optional<List<MerchantUser>> findByLeadOwnerIdsAndBussinessName(String leadOwnerId, String name);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'createdDate': {$gt:?1, $lt: ?2}, 'isOnboarded': ?3}", count = true)
    long findByLeadOwnerIdAndApprovedDateAndOnBoardedTilMtd(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnboarded);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }], 'createdDate': {$gt:?1, $lt: ?2}, 'isActivated': ?3 }", count = true)
    long findByLeadOwnerIdAndApprovedDateAndActivatedTilMtd(String leadOwnerId, Instant fromDate,Instant endDate,
                                                            Boolean isActivated);

    @Query(value = "{'$or':[ {'params.leadOwnerId': ?0 }, {'params.leadOwnerIds': ?0 }]}", fields = "{'shopName':  1, 'displayId':  1, '_id': 1}")
    Optional<List<MerchantUser>> findByMerchantNameAndLeadOwnerId(String leadOwnerId);

    @Query(value = "{'mobile': ?0, 'status': {'$ne': ?1}}", count = true)
    long getPartnerMerchantStatusCount(String mobile, String status);


    @Query(value = "{'$or':[ {'params.brandId':?0 },{ 'params.brandIds':?0} ]}", fields = "{'_id':  1}")
    Optional<List<MerchantUser>> findByBrandId(String brandId);

    @Query(value = "{'shopName': {'$regex' : ?0 , $options: 'i'}}")
    Optional<MerchantUser> findByShopName(String name);

    @Query(value = "{'securityCredentials.xApiKey': ?0}")
    Optional<MerchantUser> findByXApiKey(String xApiKey);
}
