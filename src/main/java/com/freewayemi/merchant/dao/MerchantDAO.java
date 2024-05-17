package com.freewayemi.merchant.dao;

import com.freewayemi.merchant.dto.sales.MerchantCountReq;
import com.freewayemi.merchant.dto.sales.MerchantUserAndCountResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.utils.MerchantCommonUtil;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MerchantDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantDAO.class);
    private final MongoTemplate mongoTemplate;
    private final MerchantUserRepository merchantUserRepository;

    @Autowired
    public MerchantDAO(MongoTemplate mongoTemplate, MerchantUserRepository merchantUserRepository) {
        this.mongoTemplate = mongoTemplate;
        this.merchantUserRepository = merchantUserRepository;
    }

    public List<MerchantUser> getMerchantUsersWithFilter(String leadOwnerId, String text, String status, int skip,
                                                         int limit) {

        Query query = new Query();

        if (MerchantCommonUtil.isNotEmptyString(text)) {
            Criteria criteria = new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                    Criteria.where("params.leadOwnerId").is(leadOwnerId));
            Criteria paramsCriteria = new Criteria().orOperator(Criteria.where("mobile").is(text),
                    Criteria.where("mobile").is(text), Criteria.where("email").is(text),
                    Criteria.where("displayId").is(text), Criteria.where("shopName").is(text));
            query.addCriteria(new Criteria().andOperator(criteria, paramsCriteria));
        } else {
            query.addCriteria(new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                    Criteria.where("params.leadOwnerId").is(leadOwnerId)));
        }
        if (MerchantCommonUtil.isNotEmptyString(status) && !"".equals(status)) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));
        query.skip(skip);
        query.limit(limit);
        return mongoTemplate.find(query, MerchantUser.class);
    }


    public MerchantUserAndCountResponse getMerchantInfosv3(String leadOwnerId, String text, String status,
                                                           String searchType, String fieldNe, String transacting,
                                                           int skip, int limit) {
        Query query = new Query();
        if (MerchantCommonUtil.isNotEmptyString(text)) {
            Criteria ownerCreteria = new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                    Criteria.where("params.leadOwnerId").is(leadOwnerId));
            Criteria paramsCriteria = new Criteria().orOperator(Criteria.where("mobile").is(text),
                    Criteria.where("mobile").is(text), Criteria.where("email").is(text),
                    Criteria.where("displayId").is(text), Criteria.where("shopName").regex(text, "i"));
            query.addCriteria(new Criteria().andOperator(ownerCreteria, paramsCriteria));
        } else {
            query.addCriteria(new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                    Criteria.where("params.leadOwnerId").is(leadOwnerId)));
        }
        if (MerchantCommonUtil.isNotEmptyString(status)) {
            if (MerchantStatus.approved.name().equals(status)) {
                query.addCriteria(Criteria.where("isOnboarded").exists(false));
            }
            if (MerchantStatus.onboarded.name().equals(status)) {
                query.addCriteria(Criteria.where("isOnboarded").is(Boolean.TRUE));
                query.addCriteria(Criteria.where("status").is(MerchantStatus.approved));
            } else {
                query.addCriteria(Criteria.where("status").is(status));
            }
        }
        if (MerchantCommonUtil.isNotEmptyString(fieldNe)) {
            query.addCriteria(Criteria.where(fieldNe).ne(Boolean.TRUE));
            if (MerchantStatus.isActivated.name().equals(fieldNe)) {
                query.addCriteria(Criteria.where("isOnboarded").is(Boolean.TRUE));
            }
            if (MerchantStatus.isOnboarded.name().equals(fieldNe)) {
                query.addCriteria(Criteria.where("status").is(MerchantStatus.approved));
            }
        }
        LOGGER.info("query: {}", query);
        long count = mongoTemplate.count(query, MerchantUser.class);
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));
        query.skip(skip);
        query.limit(limit);
        List<MerchantUser> merchantUsers = mongoTemplate.find(query, MerchantUser.class);
        return MerchantUserAndCountResponse.builder().merchantCount(count).merchantUsers(merchantUsers).build();
    }


    public List<MerchantUser> searchMerchants(String leadOwnerId, String params) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                Criteria.where("params.leadOwnerId").is(leadOwnerId));
        Criteria paramsCriteria = new Criteria().orOperator(Criteria.where("mobile").is(params),
                Criteria.where("email").is(params), Criteria.where("displayId").is(params),
                Criteria.where("shopName").regex(params, "i"));
        query.addCriteria(new Criteria().andOperator(criteria, paramsCriteria));
        LOGGER.info("search merchants query : {}", query);
        return mongoTemplate.find(query, MerchantUser.class);
    }

    public long getMerchantCounts(MerchantCountReq merchantCountReq) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("params.leadOwnerId").is(merchantCountReq.getLeadOwnerId()), Criteria.where("params.leadOwnerIds").is(merchantCountReq.getLeadOwnerId()));
        Criteria criteria1 = new Criteria().andOperator(Criteria.where("isOnboarded").is(Boolean.TRUE), Criteria.where("profileDate").gt(merchantCountReq.getStartDate()).lt(merchantCountReq.getEndDate()));
        query.addCriteria(new Criteria().andOperator(criteria, criteria1));
        LOGGER.info("Check for merchant Count: {}", query);
        return mongoTemplate.count(query, MerchantUser.class);
    }

    public void updateOnboardingStatus(String merchantId, Boolean isOnBoarded) {
        merchantUserRepository.findById(merchantId).ifPresent(merchantUser -> {
            merchantUser.setIsOnboarded(Boolean.TRUE);
            merchantUserRepository.save(merchantUser);
        });
    }


    public List<String> getUniqueMerchantIds(String leadOwnerId) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("params.leadOwnerIds").is(leadOwnerId),
                Criteria.where("params.leadOwnerId").is(leadOwnerId));
        query.addCriteria(Criteria.where("isOnboarded").is(Boolean.TRUE));
        query.addCriteria(Criteria.where("status").is("approved"));
        query.addCriteria(criteria);
        List<ObjectId> ids = mongoTemplate.findDistinct(query, "_id", MerchantUser.class, ObjectId.class);
        return ids.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public List<MerchantUser> getMerchantsByShopNameWithRegex(String shopName){
        Criteria criteria = new Criteria();
        criteria.and("shopName").regex(".*" + shopName + ".*","i");
        Query query = new Query(criteria);
        return mongoTemplate.find(query, MerchantUser.class);
    }
}
