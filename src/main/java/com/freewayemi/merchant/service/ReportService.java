package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
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

import java.util.Arrays;
import java.util.Objects;


@Service
public class ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);

    private final String reportUrl;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String authKey;
    private final String source;

    @Autowired
    public ReportService(@Value("${payment.report.url}") String reportUrl,
                         @Value("${payment.report.api-key}") String apiKey,
                         @Value("${payment.report.auth-key}") String authKey,
                         @Value("${payment.report.source}") String source,CommonPropertiesManager commonPropertiesManager) {
        this.reportUrl = reportUrl;
        this.apiKey = apiKey;
        this.authKey = authKey;
        this.source = source;
        this.restTemplate=commonPropertiesManager.getRestTemplate();
    }

    @Async
    public void createEvent(CreateEventRequest createEventRequest) {
        HttpHeaders headers = GetHeaders();
        try {
            HttpEntity<CreateEventRequest> entity = new HttpEntity<>(createEventRequest, headers);
            LOGGER.info("calling report service to createEvent with params: {}", entity);
            ResponseEntity<String> response =
                    restTemplate.exchange(reportUrl + "/event/createEvent", HttpMethod.POST, entity, String.class);
            LOGGER.info("Received response for create event: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Event created successfully");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
