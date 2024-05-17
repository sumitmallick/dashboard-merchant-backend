package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.ConsumerCardResponse;
import com.freewayemi.merchant.commons.dto.PgConsumerPaymentRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.ConsumerMSConstants;
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

@Component
public class ConsumerProfileServiceBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    private final RestTemplate restTemplate;
    private static final String CONSUMER_MS_BASE_URL = "http://consumerms/consumer";

    @Autowired
    public ConsumerProfileServiceBO(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ConsumerCardResponse getOrCreateCard(String consumerId, PgConsumerPaymentRequest pgConsumerPaymentRequest) {
        try {
            String url = String.format(CONSUMER_MS_BASE_URL + ConsumerMSConstants.GET_OR_CREATE_CARD_URL, consumerId);
            HttpEntity<PgConsumerPaymentRequest> entity = new HttpEntity<>(pgConsumerPaymentRequest);
            ResponseEntity<ConsumerCardResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, ConsumerCardResponse.class);
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
            LOGGER.error("Exception occurred while getting or creating card for consumer id:" + consumerId, e);
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!", "consumer", consumerId);
    }

}
