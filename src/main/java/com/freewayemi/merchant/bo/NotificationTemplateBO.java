package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.NotificationEvent;
import com.freewayemi.merchant.entity.NotificationTemplate;
import com.freewayemi.merchant.repository.NotificationTemplateRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationTemplateBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTemplateBO.class);

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    public NotificationTemplateBO(NotificationTemplateRepository notificationTemplateRepository) {
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public List<NotificationTemplate> getByEventName(String eventName) {
        return notificationTemplateRepository.findByEventNameAndActive(eventName, Boolean.TRUE)
                .orElse(new ArrayList<>());
    }

    public List<NotificationTemplate> getNotificationTemplates(NotificationEvent notificationEvent) {
        if (StringUtils.isBlank(notificationEvent.getEventName())) {
            LOGGER.error("Received empty event name");
            return new ArrayList<>();
        }
        List<NotificationTemplate> templates = getByEventName(notificationEvent.getEventName());
        if (CollectionUtils.isEmpty(templates)) {
            LOGGER.info("No notification templates found for eventName: {}", notificationEvent.getEventName());
        }
        return templates;
    }

}
