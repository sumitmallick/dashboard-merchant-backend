package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.deliveryorder.DeliveryOrderResp;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.utils.Util;

import com.freewayemi.merchant.dto.PaymentProviderTransactionResponseV2;

import com.freewayemi.merchant.dto.ConsentRequest;
import com.freewayemi.merchant.dto.ConsumerCreationRequest;
import com.freewayemi.merchant.dto.ConsumerProfileResponse;
import com.freewayemi.merchant.dto.response.ConsumerProfileConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.freewayemi.merchant.commons.utils.ConsumerMSConstants.PROCESS_SEAMLESS_TRANSACTION_URL;
import static com.freewayemi.merchant.commons.utils.ConsumerMSConstants.PROFILE_CONSTANTS_URL;

@Component
public class ConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    private final RestTemplate restTemplate;
    private static final String CONSUMER_URL = "http://consumerms/consumer/api/v1/";
    private static final String CONSUMER_V2_TRANSACTION_URL = "http://consumerms/consumer/api/v2/transactions/";
    private static final String CONSUMER_MS_BASE_URL = "http://consumerms/consumer";
    private final JwtTokenBO jwtTokenBO;

    private final String consumerApiKey;
    private final String consumerAuthKey;

    @Autowired
    public ConsumerService(RestTemplate restTemplate, JwtTokenBO jwtTokenBO, @Value("${consumer.api.key}") String consumerApiKey, @Value("${consumer.auth.key}") String consumerAuthKey) {
        this.restTemplate = restTemplate;
        this.jwtTokenBO = jwtTokenBO;
        this.consumerApiKey = consumerApiKey;
        this.consumerAuthKey = consumerAuthKey;
    }

    public ConsumerResponse updateConsumerProfile(String mobile, ProfileRequest profileRequest) {
        HttpEntity<ProfileRequest> entity = new HttpEntity<>(profileRequest, populateHeaders());
        try {
            String url = CONSUMER_URL + "consumers/" + mobile + "/profile";
            ResponseEntity<ConsumerResponse> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, ConsumerResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
    }

    public ConsumerResponse getOrCreateConsumer(String mobile) {
        return getOrCreateConsumer(mobile, null);
    }

    public ConsumerResponse getOrCreateConsumer(String mobile, String email) {
        try {
            String url = null == email ? CONSUMER_URL + "consumer/" + mobile
                    : CONSUMER_URL + "consumer/" + mobile + "?email=" + email;
            ResponseEntity<ConsumerResponse> response = restTemplate.getForEntity(url, ConsumerResponse.class);
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
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public ConsumerCardResponse getOrCreateCard(String consumerId, PgConsumerPaymentRequest pgConsumerPaymentRequest) {
        HttpEntity<PgConsumerPaymentRequest> entity = new HttpEntity<>(pgConsumerPaymentRequest);
        try {
            String url = CONSUMER_URL + "consumer/cards/" + consumerId;
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
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!", "consumer", consumerId);
    }

    public ConsumerResponse getConsumerInfo(String consumerId) {
        try {
            String url = CONSUMER_URL + "consumer/consumerInfo/" + consumerId;
            ResponseEntity<ConsumerResponse> response = restTemplate.getForEntity(url, ConsumerResponse.class);
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
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public ConsumerProfileResponse getConsumerProfile(ConsumerCreationRequest consumerCreationRequest, String merchantId) {
        try {
            String url = CONSUMER_MS_BASE_URL + "/internal/api/v1/consumer/profile";
            HttpHeaders headers = populateHeadersWithApiKeys();
            headers.add("X-SOURCE", "MERCHANTMS");
            headers.add("X-SOURCE-ID", merchantId);
            HttpEntity<ConsumerCreationRequest> entity = new HttpEntity<>(consumerCreationRequest, headers);
            ResponseEntity<ConsumerProfileResponse> response = restTemplate.postForEntity(url, entity, ConsumerProfileResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred:" + e);
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}", e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (Exception e) {
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);

        }
        throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

    private HttpHeaders populateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders populateHeadersWithApiKeys() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", consumerApiKey);
        headers.add("X-AUTH-KEY", consumerAuthKey);
        return headers;
    }

    public PaymentProviderTransactionResponse getSeamlessTransactionResponse(String transactionId, String consumerId) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization",
                    "Bearer " + jwtTokenBO.generateTempToken(consumerId, Collections.singletonList("PAYMENT")));
            HttpEntity<String> entity = new HttpEntity<>("", httpHeaders);
            String url = CONSUMER_V2_TRANSACTION_URL + transactionId + "/seamless";
            ResponseEntity<PaymentProviderTransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, PaymentProviderTransactionResponse.class);
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
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    @Async
    public void qwikOrder(String uuid, GiftVoucherRequest request) {
        LOGGER.info("calling with uuid: {} and request: {}", uuid, request);
        HttpEntity<GiftVoucherRequest> entity = new HttpEntity<>(request, populateHeaders());
        try {
            String url = CONSUMER_URL + "vouchers/" + uuid;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info(response.getBody());
            }
        } catch (Exception e) {
            LOGGER.error("exception occured with qwik calling: ", e);
            throw new FreewayException(e.getMessage());
        }
    }

    @Async
    public void handleConsumerCreditScore(String consumerId, String source, String sourceId) {
        if (StringUtils.hasText(consumerId)) {
            LOGGER.info("Sending request to fetch consumer id: {} credit score ", consumerId);
            String url = CONSUMER_URL + "consumers/" + consumerId + "/creditScore?source=" + source + "&sourceId=" +
                    sourceId;
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, HttpMethod.POST, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    LOGGER.info("Response received for consumer credit score: {}", response.getBody());
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred while sending fetch credit score report: {} with consumer id: {} ", e,
                        consumerId);
            }
        }
    }

//    @Async
//    public void updateConsent(String consumerId) {
//        LOGGER.info("calling update consent with consumerId: {}.", consumerId);
//        HttpEntity<String> entity = new HttpEntity<>(populateHeaders());
//        try {
//            String url = CONSUMER_URL + "consumers/"+ consumerId +"/consents/";
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity,
//                    String.class);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                LOGGER.info(response.getBody());
//            }
//        } catch (Exception e) {
//            LOGGER.error("exception occured with updating consents: ", e);
//        }
//    }

    public PaymentProviderTransactionResponse processSeamlessTransaction(String transactionId,
                                                                         PgConsumerPaymentRequest pgConsumerPaymentRequest) {
        try {
            String url = String.format(CONSUMER_MS_BASE_URL + PROCESS_SEAMLESS_TRANSACTION_URL, transactionId);
            HttpEntity<PgConsumerPaymentRequest> entity = new HttpEntity<>(pgConsumerPaymentRequest);
            LOGGER.info("Sending request process seamless transaction for transaction id: {}", transactionId);
            ResponseEntity<PaymentProviderTransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, PaymentProviderTransactionResponse.class);
            LOGGER.info("Response received for process seamless transaction for transaction id: : {}", transactionId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {} for transaction id: " + transactionId,
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {} for transaction id: " + transactionId,
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while processing seamless transaction for transaction id:" + transactionId,
                    e);
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!", "consumer", transactionId);
    }

    public PaymentProviderTransactionResponseV2 payPayment(PgConsumerPaymentRequest pgConsumerPaymentRequest, String transactionId, String consumerId) {
        try {
            String url =
                    CONSUMER_MS_BASE_URL + "/internal/api/v1/transactions/" + transactionId + "/process/" + consumerId;
            HttpEntity<PgConsumerPaymentRequest> entity = new HttpEntity<>(pgConsumerPaymentRequest, populateHeaders());
            ResponseEntity<PaymentProviderTransactionResponseV2> response = restTemplate.exchange(url,HttpMethod.PUT, entity, PaymentProviderTransactionResponseV2.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling pay payment api: ", e);
        }
        return null;
    }

    public ConsumerProfileConstants getProfileConstant() {
        try {
            String url = String.format(CONSUMER_MS_BASE_URL + PROFILE_CONSTANTS_URL);
            HttpEntity<String> entity = new HttpEntity<>(null, populateHeadersWithApiKeys());
            LOGGER.info("Sending request to consumer services to fetch profile cosntants");
            ResponseEntity<ConsumerProfileConstants> response = restTemplate.exchange(url, HttpMethod.GET, entity, ConsumerProfileConstants.class);
            LOGGER.info("Response received form consumer for profile");
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClintErrorExceptionOccurred : {}", e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {}",
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching profile constants: {}",
                    e);
            throw new FreewayException(e.getMessage());
        }
        return null;
    }

    @Async
    public void saveMobileEmailConsent(ConsentRequest consentRequest, String consumerId) {
        try {
            String url = CONSUMER_MS_BASE_URL + "/internal/api/v1/consents/" + consumerId;
            HttpEntity<ConsentRequest> entity = new HttpEntity<>(consentRequest, populateHeadersWithApiKeys());
            LOGGER.info("api call to save mobile/email consent: {} - {}", url, entity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("mobile/email consent saved successfully");
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while saving mobile/email consent " + e);
        }
    }

}
