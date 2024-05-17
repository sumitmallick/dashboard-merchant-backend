package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.NotificationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate,String> {

    Optional<List<NotificationTemplate>> findByEventNameAndActive(String eventName, Boolean active);
}
