package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.StoreLinkDetails;
import com.freewayemi.merchant.commons.dto.StoreLinkRequest;
import com.freewayemi.merchant.commons.dto.StoreLinkResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.utils.PaymentOpsPropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class StoreLinkServiceBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreLinkServiceBO.class);

    private final RestTemplate restTemplate;
    private static final String storeLinkUrl = "http://paymentms/payment/api/v1/storeLinks";
    private static final String storeLinkV2Url = "http://paymentms/payment/api/v2/storeLinks";
    private final PaymentOpsPropertyManager paymentOpsPropertyManager;
    @Autowired
    public StoreLinkServiceBO(RestTemplate restTemplate, PaymentOpsPropertyManager paymentOpsPropertyManager) {
        this.restTemplate = restTemplate;
        this.paymentOpsPropertyManager = paymentOpsPropertyManager;
    }

    public StoreLinkResponse createStoreLink(String merchantId, StoreLinkRequest request) {
        HttpEntity<StoreLinkRequest> entity = new HttpEntity<>(request);
        try {
            String url = storeLinkUrl + "/merchant/" + merchantId + "/create";
            ResponseEntity<StoreLinkResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, StoreLinkResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "merchant", merchantId);
    }

    public StoreLinkResponse createStoreLinkV2(String merchantId) {
        HttpEntity<StoreLinkRequest> entity = new HttpEntity<>(null);
        try {
            String url = storeLinkV2Url + "?mid=" + merchantId;
            ResponseEntity<StoreLinkResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, StoreLinkResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "merchant", merchantId);
    }

    /**
     * @param storeLinkId
     * @return
     */
    public StoreLinkDetails getStoreLinkDetails(String storeLinkId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-API-KEY",paymentOpsPropertyManager.getPaymentOpsApiKeys());
            headers.add("X-AUTH-KEY",paymentOpsPropertyManager.getPaymentOpsAuthKeys());
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/storeLinks/" + storeLinkId;
            ResponseEntity<StoreLinkDetails> response = restTemplate.exchange(url,HttpMethod.GET ,httpEntity , StoreLinkDetails.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "storeLinkId", storeLinkId);
    }

    public String patchStoreLink(String storeLinkId, Map<String, String> params) {

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params);
        try {
            String url = storeLinkUrl + "/" + storeLinkId;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "storelink", storeLinkId);
    }

}
