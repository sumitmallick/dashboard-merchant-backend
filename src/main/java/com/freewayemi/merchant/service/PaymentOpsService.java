package com.freewayemi.merchant.service;

import com.freewayemi.merchant.dto.request.ProviderConfigRequest;
import com.freewayemi.merchant.dto.request.PartnerInfo;
import com.freewayemi.merchant.dto.request.PartnerInfoResponse;
import com.freewayemi.merchant.dto.request.SchemeConfigRequest;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.dto.sales.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;


@Service
public class PaymentOpsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOpsService.class);

    private final RestTemplate restTemplate;
    private final String paymentOpsUrl;
    private final String paymentUrl;
    private final String paymentops_api_key;
    private final String paymentops_auth_key;

    @Autowired
    public PaymentOpsService(@Value("${payment.paymentops.url}") String paymentOpsUrl,
                             @Value("${payment.payment.url}") String paymentUrl,
                             @Value("${paymentops.api.key}") String paymentops_api_key,
                             @Value("${paymentops.auth.key}") String paymentops_auth_key,
                             CommonPropertiesManager commonPropertiesManager) {
        this.paymentOpsUrl = paymentOpsUrl;
        this.paymentUrl = paymentUrl;
        this.paymentops_api_key = paymentops_api_key;
        this.paymentops_auth_key = paymentops_auth_key;
        this.restTemplate = commonPropertiesManager.getRestTemplate();
    }

    public PaymentConfigResponse getPgSettings(TransactionOpsRequest transactionRequest) {
        String url = paymentOpsUrl + "/internal/paymentConfig/api/v1/getByMerchantId";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionRequest, httpHeaders);
            LOGGER.info("getByMerchantId request: {} {}", url, entity);
            ResponseEntity<PaymentConfigResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, PaymentConfigResponse.class);
            LOGGER.info("getByMerchantId response: {}", response);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) &&
                    Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public void updatePaymentConfig(PaymentConfigInfo paymentConfigInfo) {
        String url = paymentUrl + "/internal/paymentConfig/api/v1/update";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<PaymentConfigInfo> entity = new HttpEntity<>(paymentConfigInfo, httpHeaders);
            LOGGER.info("updatePaymentConfigByMerchantId request: {} {}", url, entity);
            ResponseEntity<BaseResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, BaseResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) &&
                    Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                LOGGER.info("updatePaymentConfigByMerchantId successfully updated");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
    }

    public TransactionDataResponse getMerchantList(TransactionOpsRequest transactionOpsRequest) {
        String url = paymentOpsUrl + "/internal/api/v1/uniqueMerchantsList";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionOpsRequest, httpHeaders);
            LOGGER.info("uniqueMerchantsList request: {} {}", url, entity);
            ResponseEntity<TransactionDataResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionDataResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }


    public TransactionDataResponse getTransactionCount(TransactionOpsRequest transactionCountReq) {
        String url = paymentOpsUrl + "/internal/api/v1/transactionCount";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionCountReq, httpHeaders);
            LOGGER.info("getTransactionCountReq: {}", entity);
            ResponseEntity<TransactionDataResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionDataResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred" + e);
        }
        return null;
    }

    public TransactionVolumeInfo getTransactionVolume(TransactionOpsRequest transactionCountReq) {
        String url = paymentOpsUrl + "/internal/api/v1/transactionVolume";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionCountReq, httpHeaders);
            LOGGER.info("transactionVolume: {}", entity);
            ResponseEntity<TransactionVolumeInfo> response = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionVolumeInfo.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {}", e);
        } catch (Exception e) {
            LOGGER.error("Exception occurred: {}", e);
        }
        return new TransactionVolumeInfo();
    }

    public SalesDataResponse salesDataByLeadOwnerId(String devicetoken, TransactionOpsRequest transactionOpsRequest) {
        String url = paymentOpsUrl + "/internal/api/v1/salesDataByLeadOwnerId";
        try {
            HttpHeaders httpHeaders = getHeaders();
            httpHeaders.add("DeviceToken", devicetoken);
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionOpsRequest, httpHeaders);
            LOGGER.info("getTransactionDetails request: {} {}", url, entity);
            ResponseEntity<SalesDataResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, SalesDataResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public TransactionDataResponse getTransactions(TransactionOpsRequest transactionRequest) {
        String url = paymentOpsUrl + "/internal/api/v1/transactions";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<TransactionOpsRequest> entity = new HttpEntity<>(transactionRequest, httpHeaders);
            LOGGER.info("getTransactions request: {} {}", url, entity);
            ResponseEntity<TransactionDataResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, TransactionDataResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getStatus()) && response.getBody().getStatus().equals("SUCCESS")) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public ProviderMasterConfigResponse getAllProviderMasterConfig(String partner) {
        String url = paymentOpsUrl + "/internal/providerMasterConfig/api/v1/getAllProviderMasterConfig";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<String> entity = new HttpEntity<>("", httpHeaders);
            url = UriComponentsBuilder.fromUriString(url).queryParam("partnerCode", partner).toUriString();
            LOGGER.info("getAllProviderMasterConfig request: {} {}", url, entity);
            ResponseEntity<ProviderMasterConfigResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, ProviderMasterConfigResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getStatus()) && "SUCCESS".equals(response.getBody().getStatus())) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public SchemeConfigResponse getMasterData(SchemeConfigRequest schemeConfigRequest) {
        String url = paymentOpsUrl + "/internal/configData/api/v1/getMasterData";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<SchemeConfigRequest> entity = new HttpEntity<>(schemeConfigRequest, httpHeaders);
            LOGGER.info("getAllProviderMasterConfig request: {} {}", url, entity);
            ResponseEntity<SchemeConfigResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, SchemeConfigResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public TransactionDOResponse getTransactionDO(String merchantId) {

        String url = paymentOpsUrl + "/internal/api/v1/getDOByTransactionId/"+merchantId;

        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity entity = new HttpEntity<>(httpHeaders);
            LOGGER.info("getTransactionDO for merchantId: {}", merchantId);
            ResponseEntity<TransactionDOResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, TransactionDOResponse.class);
            LOGGER.info("getTransactionDO for merchantId: {} and response: {}", merchantId, response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public BankDetailsResponse getBankDetails() {
        String url = paymentOpsUrl + "/api/v1/bankInfo/getBankDetails";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<BankDetailsResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, BankDetailsResponse.class);
            LOGGER.debug("getBankDetailsResponse: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", paymentops_api_key);
        headers.add("X-AUTH-KEY", paymentops_auth_key);
        return headers;
    }

    public PaymentProviderResponse getAllProviderMappings() {
        String url = paymentOpsUrl + "/internal/ppcbMapping/api/v1/getAll";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity entity = new HttpEntity<>(httpHeaders);
            LOGGER.info("Get All providers mappings");
            ResponseEntity<PaymentProviderResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PaymentProviderResponse.class);
            LOGGER.info("get All provider mappings response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public ProviderConfigResponse getProviderConfig(ProviderConfigRequest providerConfigRequest) {
        String url = paymentOpsUrl + "/eligibility/internal/api/v2/providerconfig/get";
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity<ProviderConfigRequest> entity = new HttpEntity<>(providerConfigRequest, httpHeaders);
            LOGGER.info("Get provider config with bands");
            ResponseEntity<ProviderConfigResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ProviderConfigResponse.class);
            LOGGER.info("gGet provider config with bands response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    public PartnerInfoResponse getPartnerInfo(String partner){
        String url = paymentOpsUrl + "/internal/partnerInfo/api/v1/" + partner;
        try {
            HttpHeaders httpHeaders = getHeaders();
            HttpEntity entity = new HttpEntity<>(httpHeaders);
            LOGGER.info("Get partner details: {}", entity);
            ResponseEntity<PartnerInfoResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PartnerInfoResponse.class);
            LOGGER.info("partner info response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }
}
