package com.freewayemi.merchant.service.notifications;

import com.freewayemi.merchant.bo.NotificationBO;
import com.freewayemi.merchant.bo.NotificationTemplateBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.SendNotificationRequest;
import com.freewayemi.merchant.dto.NotificationEvent;
import com.freewayemi.merchant.entity.Notification;
import com.freewayemi.merchant.entity.NotificationTemplate;
import com.freewayemi.merchant.pojos.ResolvedNotification;
import com.freewayemi.merchant.type.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBO.class);

    private final NotificationTemplateBO notificationTemplateBO;
    private final NotificationTemplateResolver notificationTemplateResolver;
    private final NotificationService notificationService;
    private final NotificationBO notificationBO;

    @Autowired
    public NotificationEventService(NotificationTemplateBO notificationTemplateBO,
                                    NotificationTemplateResolver notificationTemplateResolver,
                                    NotificationService notificationService, NotificationBO notificationBO) {
        this.notificationTemplateBO = notificationTemplateBO;
        this.notificationTemplateResolver = notificationTemplateResolver;
        this.notificationService = notificationService;
        this.notificationBO = notificationBO;
    }

    @Async
    public void handleNotificationEvent(NotificationEvent notificationEvent) {
        List<NotificationTemplate> templates = notificationTemplateBO.getNotificationTemplates(notificationEvent);
        for (NotificationTemplate template: templates) {
            try {
                ResolvedNotification resolvedNotification = notificationTemplateResolver.resolve(notificationEvent,
                        template);
                if (resolvedNotification.isResolveSuccess()) {
                    Notification notification = createNotification(template, resolvedNotification);
                    notification = notificationBO.saveNotification(notification);
                    if (resolvedNotification.isSendNotification()) {
                        LOGGER.info("Sending {} notification with id: {}", notificationEvent.getEventName(),
                                notification.getId().toString());
                        sendNotification(template, resolvedNotification);
                    } else {
                        LOGGER.info("Skipping {} notification with id: {}", notificationEvent.getEventName(),
                                notification.getId().toString());
                    }
                } else {
                    LOGGER.error("Template resolution failed for template id: {}", template.getId().toString());
                }
            } catch (Exception e) {
                LOGGER.error("Exception while handling notification template: {} eventName: {}",
                        template.getId().toString(), template.getEventName());
                LOGGER.error("Notification template exception", e);
            }
        }
    }

    private void sendNotification(NotificationTemplate template, ResolvedNotification resolvedNotification) {
        Map<String, Object> data = new HashMap<>();
        if (resolvedNotification.getChannelsInfo() != null) {
            data.putAll(resolvedNotification.getChannelsInfo());
        }
        if (resolvedNotification.getData() != null) {
            data.putAll(resolvedNotification.getData());
        }
        LOGGER.debug("Sending notification with data: {}", data);
        notificationService.send(new SendNotificationRequest(template.getChannels(), template.getType(), data));
    }

    private Notification createNotification(NotificationTemplate template, ResolvedNotification resolvedNotification) {
        String source = template.getAppType() != null ?
                AppType.SALES_APP.equals(template.getAppType()) ? "sales_app" : template.getAppType().name() : null;
        Notification notification = Notification.builder()
                .active(resolvedNotification.isSendNotification())
                .readStatus(false)
                .source(source)
                .eventName(template.getEventName())
                .channelsInfo(resolvedNotification.getChannelsInfo())
                .leadOwnerId(resolvedNotification.getLeadOwnerId())
                .merchantId(resolvedNotification.getMerchantId())
                .groups(template.getGroups())
                .build();

        if (!resolvedNotification.isSendNotification()) {
            notification.setSentNotification(false);
        }

        Map<String, Object> otherData = new HashMap<>();
        for (Map.Entry<String, Object> entry: resolvedNotification.getData().entrySet()) {
            switch (entry.getKey()) {
                case "title":
                    notification.setTitle(entry.getValue().toString());
                    break;
                case "body":
                    notification.setBody(entry.getValue().toString());
                    break;
                case "custom_data":
                    if (entry.getValue() instanceof Map) {
                        notification.setData((Map<String, Object>) entry.getValue());
                    } else {
                        otherData.put(entry.getKey(), entry.getValue());
                    }
                    break;
                default:
                    otherData.put(entry.getKey(), entry.getValue());
            }
        }
        if (otherData.size() > 0) {
            notification.setOtherData(otherData);
        }
        return notification;
    }
}
