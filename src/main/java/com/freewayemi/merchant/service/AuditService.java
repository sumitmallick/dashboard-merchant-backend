package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.AuditNotificationRequest;
import com.freewayemi.merchant.dto.request.CreateEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.freewayemi.merchant.utils.Constants.DESTINATION;


@Service
public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    private final String auditUrl;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String authKey;
    private final String source;

    @Autowired
    public AuditService(@Value("${payment.audit.url}") String auditUrl,
                        @Value("${payment.audit.api-key}") String apiKey,
                        @Value("${payment.audit.auth-key}") String authKey,
                        @Value("${payment.audit.source}") String source,CommonPropertiesManager commonPropertiesManager) {
        this.auditUrl = auditUrl;
        this.apiKey = apiKey;
        this.authKey = authKey;
        this.source = source;
        this.restTemplate=commonPropertiesManager.getRestTemplate();
    }

    private Map<String, Object> convertMapStringToMapObject(Map<String, String> map) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        return newMap;
    }

    @Async
    public void prepareAndSaveSmsNotification(Map<String, String> message, String type, String channel,
                                              List<String> valueList, String receiver, String receiverId) {
        Map<String, Object> map = convertMapStringToMapObject(message);
        Map<String, Object> destination = new HashMap<>();
        destination.put("type", DESTINATION.get(channel));
        destination.put("value", map.get(DESTINATION.get(channel)));
        destination.put("valuesList", valueList);
        saveNotification(AuditNotificationRequest.builder()
                .channel(channel)
                .name(type)
                .destination(destination)
                .message(map)
                .receiver(receiver)
                .receiverId(receiverId)
                .createdDate(Instant.now())
                .build());
    }

    @Async
    public void prepareAndSavePushNotification(Map<String, String> message, String type, String channel,
                                               List<String> valueList, String landing, String messageType, String key,
                                               String mid, String receiver, String receiverId) {
        Map<String, Object> map = new HashMap<>();
        map.put("to", message.get("deviceToken"));
        map.put("collapse_key", "type_b");
        map.put("notification", new HashMap<String, Object>() {{
            put("body", message.get("body"));
            put("title", message.get("title"));
        }});
        map.put("data", new HashMap<String, Object>() {{
            put("landing", landing);
            put("key", key);
            put("mid", mid);
            put("messageType", messageType);
        }});
        Map<String, Object> destination = new HashMap<>();
        destination.put("type", DESTINATION.get(channel));
        destination.put("value", map.get(DESTINATION.get(channel)));
        destination.put("valuesList", valueList);
        saveNotification(AuditNotificationRequest.builder()
                .channel(channel)
                .name(type)
                .destination(destination)
                .message(map)
                .receiverId(receiverId)
                .receiver(receiver)
                .createdDate(Instant.now())
                .build());
    }

    public void saveNotification(AuditNotificationRequest auditNotificationRequest) {
        HttpHeaders headers = GetHeaders();
        try {
            HttpEntity<AuditNotificationRequest> entity = new HttpEntity<>(auditNotificationRequest, headers);
            LOGGER.info("calling audit service to save notification with params: {}", entity);
            ResponseEntity<String> response =
                    restTemplate.exchange(auditUrl + "/saveAuditNotification", HttpMethod.POST, entity, String.class);
            LOGGER.info("Received response for save notification: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Event created successfully");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("Error occurred while sending rewuest to Audit Service",e);
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.info("Error: {}",e.getMessage());
        }
    }

    private HttpHeaders GetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", apiKey);
        headers.add("X-AUTH-KEY", authKey);
        headers.add("X-SOURCE", source);
        return headers;
    }

}
