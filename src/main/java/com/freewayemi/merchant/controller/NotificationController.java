package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.NotificationBO;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.NotificationEvent;
import com.freewayemi.merchant.dto.PaginatedResponse;
import com.freewayemi.merchant.dto.sales.MerchantNotifications;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.MerchantAuthService;
import com.freewayemi.merchant.service.notifications.NotificationEventService;
import com.freewayemi.merchant.type.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    private final AuthCommonService authCommonService;
    private final MerchantAuthService merchantAuthService;
    private final NotificationBO notificationBO;
    private final NotificationEventService notificationEventService;


    @Autowired
    public NotificationController(AuthCommonService authCommonService, MerchantAuthService merchantAuthService,
                                  NotificationBO notificationBO, NotificationEventService notificationEventService) {
        this.authCommonService = authCommonService;
        this.merchantAuthService = merchantAuthService;
        this.notificationBO = notificationBO;
        this.notificationEventService = notificationEventService;
    }

    @GetMapping("/api/v1/{mid}/notifications/unseen/exists")
    public ResponseEntity<?> existsUnseenNotification(@PathVariable(name = "mid") String merchantId,
                                                      @RequestParam(name = "group", required = false) String group) {
        return ResponseEntity.ok(notificationBO.checkUnseenNotifications(merchantId, group, AppType.MERCHANT_APP));
    }

    @GetMapping("/api/v1/{mid}/notifications")
    public PaginatedResponse<MerchantNotifications> getMerchantNotifications(
            @PathVariable(name = "mid") String merchantId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "group", required = false) String group,
            @RequestParam(name = "eventName", required = false) String eventName,
            @RequestParam(name = "dataRequired", required = false) Boolean dataRequired) {

        LOGGER.info("Request received to get all notification for merchantId : {}, page: {}, size: {}, group: {}," +
                " eventName: {}, dataRequired: {}", merchantId, page, size, group, eventName, dataRequired);
        return notificationBO.getNotifications(merchantId, page, size, group, eventName, dataRequired,
                AppType.MERCHANT_APP);
    }

    @PostMapping("/api/v1/{mid}/notifications/seen")
    public ResponseEntity<?> seenAllNotifications(@PathVariable(name = "mid") String merchantId,
                                                  @RequestParam(name = "group", required = false) String group,
                                                  @RequestParam(name = "eventName", required = false) String eventName) {
        LOGGER.info("Request received to read all notification for merchantId : {} and group: {}", merchantId, group);
        notificationBO.seenAllNotifications(merchantId, group, eventName, AppType.MERCHANT_APP);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/{mid}/notifications")
    public ResponseEntity<?> deleteAllNotifications(@PathVariable(name = "mid") String merchantId,
                                                    @RequestParam(name = "group", required = false) String group,
                                                    @RequestParam(name = "eventName", required = false) String eventName) {
        LOGGER.info("Request received to delete all notification for merchantId : {}, group: {}", merchantId, group);
        notificationBO.inactivateAllNotifications(merchantId, group, eventName, AppType.MERCHANT_APP);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/v1/notifications/{nid}")
    public ResponseEntity<?> deleteNotification(@PathVariable(name = "nid") String notificationId) {
        LOGGER.info("Request received to delete notification: {}", notificationId);
        notificationBO.inactivateNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/api/v1/event/notify")
    public ResponseEntity<?> eventNotify(@RequestBody NotificationEvent notificationEvent,
                                         HttpServletRequest httpServletRequest) {

        LOGGER.info("Received notification event: {}", notificationEvent);
        merchantAuthService.authenticate(
                MerchantAuthDto.builder().request(httpServletRequest).source(MerchantAuthSource.INTERNAL).build());
        notificationEventService.handleNotificationEvent(notificationEvent);
        return ResponseEntity.ok().build();
    }

    // sales APIs
    @GetMapping("/internal/salesagents/api/v2/{lid}/notifications/unseen/exists")
    public ResponseEntity<?> existsUnseenNotificationSales(@PathVariable(name = "lid") String leadOwnerId,
                                                           @RequestParam(name = "group", required = false) String group) {
        return ResponseEntity.ok(notificationBO.checkUnseenNotifications(leadOwnerId, group, AppType.SALES_APP));
    }

    @GetMapping("/internal/salesagents/api/v2/{lid}/notifications")
    public PaginatedResponse<MerchantNotifications> getSalesNotifications(
            @PathVariable(name = "lid") String leadOwnerId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "group", required = false) String group,
            @RequestParam(name = "eventName", required = false) String eventName,
            @RequestParam(name = "dataRequired", required = false) Boolean dataRequired) {

        LOGGER.info("Request received to get all notification for leadOwnerId : {}, page: {}, size: {}, group: {}," +
                " eventName: {}, dataRequired: {}", leadOwnerId, page, size, group, eventName, dataRequired);
        return notificationBO.getNotifications(leadOwnerId, page, size, group, eventName, dataRequired,
                AppType.SALES_APP);
    }

    @PostMapping("/internal/salesagents/api/v2/{lid}/notifications/seen")
    public ResponseEntity<?> seenAllSalesNotifications(@PathVariable(name = "lid") String leadOwnerId,
                                                       @RequestParam(name = "group", required = false) String group,
                                                       @RequestParam(name = "eventName", required = false) String eventName) {
        LOGGER.info("Request received to read all notification for leadOwnerId : {} and group: {}", leadOwnerId, group);
        notificationBO.seenAllNotifications(leadOwnerId, group, eventName, AppType.SALES_APP);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/internal/salesagents/api/v2/{lid}/notifications")
    public ResponseEntity<?> deleteAllSalesNotifications(@PathVariable(name = "lid") String leadOwnerId,
                                                         @RequestParam(name = "group", required = false) String group,
                                                         @RequestParam(name = "eventName", required = false) String eventName) {
        LOGGER.info("Request received to delete all notification for leadOwnerId : {}, group: {}", leadOwnerId, group);
        notificationBO.inactivateAllNotifications(leadOwnerId, group, eventName, AppType.SALES_APP);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/internal/salesagents/api/v2/notifications/{nid}")
    public ResponseEntity<?> deleteSalesNotification(@PathVariable(name = "nid") String notificationId) {
        LOGGER.info("Request received to delete notification: {}", notificationId);
        notificationBO.inactivateNotification(notificationId);
        return ResponseEntity.ok().build();
    }

}
