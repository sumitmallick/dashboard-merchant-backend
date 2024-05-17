package com.freewayemi.merchant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.dto.request.MobileRuleDataRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.type.AppType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@EnableAsync
public class RuleEngineHelperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineHelperService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RuleEngineService ruleEngineService;

    @Autowired
    public RuleEngineHelperService(RuleEngineService ruleEngineService){
        this.ruleEngineService = ruleEngineService;
    }

    @Async
    public void saveMobileData(Map<String, String> headers, String referenceId) throws IOException {
        LOGGER.info("headers: {}", headers);
        if (headers.containsKey("x-device-collection")) {
            MobileRuleDataRequest mobileRuleDataRequest = objectMapper.readValue(headers.get("x-device-collection"), MobileRuleDataRequest.class);
            mobileRuleDataRequest.setReferenceId(referenceId);
            if (!(AppType.MERCHANT_APP.equals(mobileRuleDataRequest.getAppType()) || AppType.CONSUMER_APP.equals(mobileRuleDataRequest.getAppType()))) {
                return;
            }
            LOGGER.info("mobileDataRequest: {}", mobileRuleDataRequest);
            BasicResponse basicResponse = ruleEngineService.saveData(mobileRuleDataRequest);
            LOGGER.info("saving the mobile data for merchant app: {}", basicResponse);
        }
    }
}
