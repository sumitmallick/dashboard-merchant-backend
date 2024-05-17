package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dao.NotificationDAO;
import com.freewayemi.merchant.dto.PaginatedResponse;
import com.freewayemi.merchant.dto.sales.MerchantNotifications;
import com.freewayemi.merchant.entity.Notification;
import com.freewayemi.merchant.repository.NotificationRepository;
import com.freewayemi.merchant.type.AppType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NotificationBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBO.class);

    private final NotificationRepository notificationRepository;
    private final NotificationDAO notificationDAO;

    private static final int MAX_PAGE_SIZE = 20;
    private static final String SALES_APP = "sales_app";

    @Autowired
    public NotificationBO(NotificationRepository notificationRepository, NotificationDAO notificationDAO) {
        this.notificationRepository = notificationRepository;
        this.notificationDAO = notificationDAO;
    }

    public Optional<Notification> updateNotification(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public Optional<List<Notification>> getNotification(String leadOwnerId) {
        return notificationRepository.findByLeadOwnerId(leadOwnerId);
    }
    public Notification saveNotification(Notification notification){
        return notificationRepository.save(notification);
    }


    public PaginatedResponse<MerchantNotifications> getNotifications(String id, Integer page, Integer size,
                                                                     String group, String eventName,
                                                                     Boolean dataRequired, AppType appType) {

        appType = appType != null ? appType : AppType.MERCHANT_APP;
        if (StringUtils.isNotBlank(id)) {

            Pageable pageRequest = createPageRequest(page, size);

            Slice<Notification> notifications;

            // Notification group i.e. different types of notifications grouped together in one screen or context
            switch (appType) {

                case MERCHANT_APP:
                    if (StringUtils.isNotBlank(group)) {
                        notifications = StringUtils.isNotBlank(eventName) ?
                                notificationRepository.findBySourceAndMerchantIdAndGroupAndEventName(appType.name(),
                                        id, group, eventName, pageRequest) :
                                notificationRepository.findBySourceAndMerchantIdAndGroup(appType.name(), id, group,
                                        pageRequest);
                    } else {
                        notifications = StringUtils.isNotBlank(eventName) ?
                                notificationRepository.findBySourceAndMerchantIdAndEventName(appType.name(), id,
                                        eventName, pageRequest) :
                                notificationRepository.findBySourceAndMerchantId(appType.name(), id, pageRequest);
                    }
                    break;
                case SALES_APP:
                    if (StringUtils.isNotBlank(group)) {
                        notifications = StringUtils.isNotBlank(eventName) ?
                                notificationRepository.findBySourceAndLeadOwnerIdAndGroupAndEventName(SALES_APP, id,
                                        group, eventName, pageRequest) :
                                notificationRepository.findBySourceAndLeadOwnerIdAndGroup(SALES_APP, id, group,
                                        pageRequest);
                    } else {
                        notifications = StringUtils.isNotBlank(eventName) ?
                                notificationRepository.findBySourceAndLeadOwnerIdAndEventName(SALES_APP, id, eventName,
                                        pageRequest) :
                                notificationRepository.findBySourceAndLeadOwnerId(SALES_APP, id, pageRequest);
                    }
                    break;
                default:
                    throw new FreewayException(appType.name() + " AppType not supported");
            }

            // sending additional data only if requested or if specific notifications are requested
            boolean sendAdditionalData = dataRequired != null ? dataRequired : StringUtils.isNotBlank(eventName);

            return PaginatedResponse.<MerchantNotifications>builder()
                    .currentPage(notifications.getNumber())
                    .pageSize(notifications.getSize())
                    .numberOfElements(notifications.getNumberOfElements())
                    .hasNextPage(notifications.hasNext())
                    .hasPreviousPage(notifications.hasPrevious())
                    .elements(notifications.getContent().stream()
                            .map(n -> createDto(n, sendAdditionalData))
                            .collect(Collectors.toList()))
                    .build();
        }
        throw new FreewayException("Merchant Id is required for notifications");
    }

    public Map<String, Object> checkUnseenNotifications(String id, String group, AppType appType) {
        Map<String, Object> response = new HashMap<>();
        response.put("unseen", false);
        appType = appType != null ? appType : AppType.MERCHANT_APP;
        if (StringUtils.isNotBlank(id)) {
            boolean unseen;
            switch (appType) {

                case MERCHANT_APP:
                    unseen = StringUtils.isNotBlank(group) ?
                            notificationRepository.existsBySourceAndMerchantIdAndGroupAndReadStatus(appType.name(), id,
                                    group, Boolean.FALSE) :
                            notificationRepository.existsBySourceAndMerchantIdAndReadStatus(appType.name(), id,
                                    Boolean.FALSE);
                    break;
                case SALES_APP:
                    unseen = StringUtils.isNotBlank(group) ?
                            notificationRepository.existsBySourceAndLeadOwnerIdAndGroupAndReadStatus(SALES_APP, id,
                                    group, Boolean.FALSE) :
                            notificationRepository.existsBySourceAndLeadOwnerIdAndReadStatus(SALES_APP, id,
                                    Boolean.FALSE);
                    break;
                default:
                    throw new FreewayException(appType.name() + " AppType not supported");
            }
            response.put("unseen", unseen);
        }
        return response;
    }

    private MerchantNotifications createDto(Notification notification, boolean dataRequired) {
        if (notification == null) {
            return null;
        }

        return MerchantNotifications.builder()
                .id(notification.getId().toString())
                .title(notification.getTitle())
                .body(notification.getBody())
                .eventName(notification.getEventName())
                .readStatus(notification.getReadStatus())
                .createdDate(notification.getCreatedDate())
                .merchantId(notification.getMerchantId())
                .data(dataRequired ? notification.getData() : null)
                .build();
    }

    private static PageRequest createPageRequest(Integer page, Integer size) {
        int pageNo = page != null ? page : 0;
        int pageSize = Math.min(size != null ? size : MAX_PAGE_SIZE, MAX_PAGE_SIZE);
        return PageRequest.of(pageNo, pageSize, Sort.by("createdDate").descending());
    }

    @Async
    public void seenAllNotifications(String id, String group, String eventName, AppType appType) {
        if (StringUtils.isNotBlank(id)) {
            notificationDAO.markAllAsRead(id, group, eventName, appType);
        } else {
            LOGGER.error("Id missing for marking all notifications as seen");
        }
    }

    @Async
    public void inactivateAllNotifications(String id, String group, String eventName, AppType appType) {
        if (StringUtils.isNotBlank(id)) {
            notificationDAO.markAllAsInactive(id, group, eventName, appType);
        } else {
            LOGGER.error("Id missing for marking all notifications as inactive");
        }
    }

    public void inactivateNotification(String notificationId) {
        if (StringUtils.isNotBlank(notificationId)) {
            Optional<Notification> notification = notificationRepository.findById(notificationId);
            notification.ifPresent(n -> {
                n.setActive(false);
                notificationRepository.save(n);
            });
        } else {
            LOGGER.error("Notification Id missing for marking as inactive");
        }
    }

}
