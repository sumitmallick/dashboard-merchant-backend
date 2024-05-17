package com.freewayemi.merchant.service;

import com.freewayemi.merchant.dto.request.MobileRuleDataRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.dto.response.DeviceIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class RuleEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineService.class);

    private final String ruleEngineUrl;
    private final RestTemplate restTemplate;
    private final String ruleEngineApiKey;

    private final String ruleEngineAuthKey;

    @Autowired
    public RuleEngineService(RestTemplate restTemplate,
                             @Value("${mobile.rule.engine.url}") String ruleEngineUrl,
                             @Value("${mobile.rule.engine.api.key}") String ruleEngineApiKey,
                             @Value("${mobile.rule.engine.auth.key}") String ruleEngineAuthKey) {
        this.ruleEngineUrl = ruleEngineUrl;
        this.ruleEngineApiKey = ruleEngineApiKey;
        this.ruleEngineAuthKey = ruleEngineAuthKey;
        this.restTemplate = restTemplate;
    }

    public DeviceIdResponse getDeviceId() {
        String url = ruleEngineUrl + "/internal/api/v1/deviceId";
        try {
            HttpHeaders httpHeaders = GetHeaders();
            HttpEntity<String> entity = new HttpEntity<>("", httpHeaders);
            LOGGER.info("getDeviceId request: {} {}", url, entity);
            ResponseEntity<DeviceIdResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, DeviceIdResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public BasicResponse saveData(MobileRuleDataRequest mobileRuleDataRequest) {
        String url = ruleEngineUrl + "/internal/api/v1/saveData";
        try {
            HttpHeaders httpHeaders = GetHeaders();
            HttpEntity<MobileRuleDataRequest> entity = new HttpEntity<>(mobileRuleDataRequest, httpHeaders);
            LOGGER.info("saveData request: {} {}", url, entity);
            ResponseEntity<BasicResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, BasicResponse.class);
            LOGGER.info("saveData response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }


    private HttpHeaders GetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", ruleEngineApiKey);
        headers.add("X-AUTH-KEY", ruleEngineAuthKey);
        return headers;
    }
}
