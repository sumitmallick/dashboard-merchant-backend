package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.AdminAuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminAuthUserRepository extends MongoRepository<AdminAuthUser, String> {
    Optional<List<AdminAuthUser>> findByMerchantId(String merchantId);

    Optional<AdminAuthUser> findByMobile(String mobile);

    @Query("{'$or':[ {'mobile': ?0},{ 'login':?1}]}")
    Optional<AdminAuthUser> findByMobileAndEmail(String mobile, String email);

    @Query("{'partner': {$exists: ?0}}")
    Optional<List<AdminAuthUser>> findByPartnerExists(Boolean exists);

    Optional<AdminAuthUser> findByLogin(String email);

    Optional<AdminAuthUser> findByMobileAndStatus(String merchantId, String status);

    @Query("{ 'merchantId':?0  , 'permissions': ?1 }")
    Optional<List<AdminAuthUser>> findByMerchantIdAndPermissions(String merchantId, String permissions);

    Optional<AdminAuthUser> findAdminAuthUserByMobileAndPartner(String mobile, String partner);

    @Query(value = "{ 'reporter':?0}", count = true)
    long findCountByReporter(String leadOwnerId);

    @Query("{'$or':[ {'_id': ?0}, {'mobile': ?0}, {'login': ?0}]}")
    Optional<List<AdminAuthUser>> findByIdOrMerchantId(String merchantId);

    @Query("{'$or':[ {'_id': ?0}, {'mobile': ?0}, {'login': ?0}],'status':{'$in': ?1}}")
    Optional<List<AdminAuthUser>> findByIdOrMerchantIdAndStatus(String merchantId, List<String> statuses);

    @Query("{'$or':[ {'_id': ?0}, {'mobile': ?0}, {'login': ?0}], 'role':?1}")
    Optional<List<AdminAuthUser>> findByIdOrMerchantIdAndRole(String merchantId, String role);

    @Query("{'$or':[ {'_id': ?0}, {'mobile': ?0}, {'login': ?0}], 'role':?1, 'status':{'$in': ?2}}")
    Optional<List<AdminAuthUser>> findByIdOrMerchantIdAndRoleAndStatus(String merchantId, String role,
                                                                       List<String> statuses);
    Optional<List<AdminAuthUser>> findByMerchantIdAndRole(String merchantId,String role);

    @Query("{'_id': ?0, 'mobile': ?1, 'role': ?2, 'status': ?3}")
    Optional<AdminAuthUser> findByIdAndMerchantIdAndRoleAndStatus(String merchantId, String mobile, String role,
                                                                        String status);

    @Query(value = "{'merchantId': ?0, 'role': ?1, 'status':  {'$ne': ?2}}", count = true)
    long findStoreUserCount(String merchantId, String role, String status);
}
