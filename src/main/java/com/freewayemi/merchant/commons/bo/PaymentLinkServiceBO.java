package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.PaymentLinkDetails;
import com.freewayemi.merchant.commons.dto.PaymentLinkRequest;
import com.freewayemi.merchant.commons.dto.PaymentLinkResponse;
import com.freewayemi.merchant.commons.dto.ProfileRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PaymentLinkServiceBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentLinkServiceBO.class);

    private final RestTemplate restTemplate;
    private static final String paymentLinkUrl = "http://paymentms/payment/api/v1/paymentLinks";

    @Autowired
    public PaymentLinkServiceBO(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentLinkResponse createPaymentLink(PaymentLinkRequest request) {
        HttpEntity<PaymentLinkRequest> entity = new HttpEntity<>(request);
        try {
            ResponseEntity<PaymentLinkResponse> response = restTemplate.exchange(paymentLinkUrl, HttpMethod.POST,
                    entity, PaymentLinkResponse.class);
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
        throw new FreewayException("Bad Request.");
    }

    public PaymentLinkResponse getPaymentLink(String merchantId, String orderId) {
        try {
            String url = paymentLinkUrl + "/merchant/" + merchantId + "/order/" + orderId;
            ResponseEntity<PaymentLinkResponse> response = restTemplate.getForEntity(url, PaymentLinkResponse.class);
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
        throw new FreewayException("Bad Request.");
    }

    public PaymentLinkResponse patchPaymentLink(String payUid, Map<String, String> params) {
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params);
        try {
            String url = paymentLinkUrl + "/" + payUid;
            ResponseEntity<PaymentLinkResponse> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, entity, PaymentLinkResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error(
                    "HttpClientErrorException occurred while patching states of payment link with uuid: {}" + payUid,
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error(
                    "HttpServerErrorException occurred while patching states of payment link with uuid: {}" + payUid,
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "paymentLink", payUid);
    }


    public PaymentLinkDetails updatePaymentLinkConsumerProfile(String paymentLinkId, ProfileRequest request) {
        HttpEntity<ProfileRequest> entity = new HttpEntity<>(request);
        try {
            String url = paymentLinkUrl + "/" + paymentLinkId + "/consumerProfile";
            LOGGER.info("Request received to update paymentLink consumer info for paymentLinkId: {} is: {}",
                    paymentLinkId, request);
            ResponseEntity<PaymentLinkDetails> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, PaymentLinkDetails.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            LOGGER.info("Response received to update paymentLink consumer info for paymentLinkId: {} is: {}",
                    paymentLinkId, request);
        } catch (HttpClientErrorException e) {
            LOGGER.error(
                    "HttpClientErrorException occurred while update paymentLink consumer info with paymentLinkId: {}" +
                            paymentLinkId, e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error(
                    "HttpServerErrorException occurred while update paymentLink consumer info with paymentLinkId: {}" +
                            paymentLinkId, e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!!", "paymentLink", paymentLinkId);
    }
}
