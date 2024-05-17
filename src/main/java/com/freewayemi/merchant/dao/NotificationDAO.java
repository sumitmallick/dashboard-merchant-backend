package com.freewayemi.merchant.dao;

import com.freewayemi.merchant.entity.Notification;
import com.freewayemi.merchant.type.AppType;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class NotificationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDAO.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public NotificationDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void markAllAsRead(String id, String group, String eventName, AppType appType) {
        Query query = createIdQuery(id, appType);
        if (StringUtils.isNotBlank(group)) {
            query.addCriteria(Criteria.where("groups").is(group));
        }
        if (StringUtils.isNotBlank(eventName)) {
            query.addCriteria(Criteria.where("eventName").is(eventName));
        }
        query.addCriteria(Criteria.where("readStatus").is(false));

        Update update = new Update();
        update.set("readStatus", true);
        update.set("lastModifiedDate", Instant.now());

        UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);
        LOGGER.info("{} notifications marked as read for {}: {}", result.getModifiedCount(), appType, id);
    }

    public void markAllAsInactive(String id, String group, String eventName, AppType appType) {
        Query query = createIdQuery(id, appType);
        if (StringUtils.isNotBlank(group)) {
            query.addCriteria(Criteria.where("groups").is(group));
        }
        if (StringUtils.isNotBlank(eventName)) {
            query.addCriteria(Criteria.where("eventName").is(eventName));
        }
        query.addCriteria(Criteria.where("active").is(true));

        Update update = new Update();
        update.set("active", false);
        update.set("lastModifiedDate", Instant.now());

        UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);
        LOGGER.info("{} notifications marked as inactive for {}: {}", result.getModifiedCount(), appType, id);
    }

    private static Query createIdQuery(String id, AppType appType) {
        appType = appType != null ? appType : AppType.MERCHANT_APP;
        Query query = new Query();
        switch (appType) {

            case MERCHANT_APP:
                query.addCriteria(Criteria.where("source").is(appType.name()));
                query.addCriteria(Criteria.where("merchantId").is(id));
                break;
            case SALES_APP:
                query.addCriteria(Criteria.where("source").is("sales_app"));
                query.addCriteria(Criteria.where("leadOwnerId").is(id));
                break;
        }
        return query;
    }
}
