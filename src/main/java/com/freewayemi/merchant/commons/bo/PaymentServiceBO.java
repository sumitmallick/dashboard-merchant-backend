package com.freewayemi.merchant.commons.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityRequest;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.deliveryorder.DeliveryOrderResp;
import com.freewayemi.merchant.commons.dto.downpayment.DownPaymentConfigDto;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutRequest;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundInquiryResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.PostPaymentResponse;
import com.freewayemi.merchant.dto.ValidateOtpRequest;
import com.freewayemi.merchant.dto.paymentOptions.PaymentOptionsRequest;
import com.freewayemi.merchant.dto.paymentOptions.PaymentOptionsResponse;
import com.freewayemi.merchant.dto.request.ProviderConfigRequest;
import com.freewayemi.merchant.dto.response.EligibilityMSResponse;

import com.freewayemi.merchant.dto.response.ProviderConfigResponse;

import com.freewayemi.merchant.dto.response.EnquiryTransactionResponse;

import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.CommonPropertiesManager;
import com.freewayemi.merchant.utils.PaymentOpsPropertyManager;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.*;

@Component
public class PaymentServiceBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceBO.class);
    private static final String PAYMENT_URL = "http://paymentms/payment/api/v1/";
    private static final String PAYMENT_BASE_URL = "http://paymentms/payment";
    private static final String PAYMENT_INTERNAL_URL = "http://paymentms/payment/internal/api/v1/";
    private final String emsAPIBaseUrl;
    private final String emsDiscoveryBaseUrl;
    private final Boolean isEMSCallViaDiscovery;
    private final String emsEligibilityCheckUrl;
    private final RestTemplate restTemplate;
    private final Boolean isEligibilityMSAPIEnabled;
    private final String ntbservices_internal_url;
    private final ObjectMapper om;
    private final String ntbservices_api_key;
    private final String ntbservices_auth_key;
    private final PaymentOpsPropertyManager paymentOpsPropertyManager;
    private final RestTemplate paymentOpsRestTemplate;
    private final String eligibilityAuthKey;
    private final String eligibilityApiKey;

    @Autowired
    public PaymentServiceBO(@Value("${eligibility.service.api.base.url}") String emsAPIBaseUrl,
                            @Value("${eligibility.service.discovery.base.url}") String emsDiscoveryBaseUrl,
                            @Value("${is.eligibility.service.call.via.discovery}") Boolean isEMSCallViaDiscovery,
                            @Value("${eligibility.service.eligibility.check.url}") String emsEligibilityCheckUrl,
                            RestTemplate restTemplate,
                            @Value("${eligibility.service.api.enable}") Boolean isEligibilityMSAPIEnabled,
                            @Value("${ntbservices.api.key}") String ntbservices_api_key,
                            @Value("${ntbservices.auth.key}") String ntbservices_auth_key,
                            ObjectMapper om, PaymentOpsPropertyManager paymentOpsPropertyManager,
                            CommonPropertiesManager commonPropertiesManager,
                            @Value("${payment.ntbservices.url}") String ntbservices_internal_url,
                            @Value("${eligibility.api.key}") String eligibilityApiKey,
                            @Value("${eligibility.auth.key}") String eligibilityAuthKey) {
        this.emsAPIBaseUrl = emsAPIBaseUrl;
        this.emsDiscoveryBaseUrl = emsDiscoveryBaseUrl;
        this.isEMSCallViaDiscovery = isEMSCallViaDiscovery;
        this.emsEligibilityCheckUrl = emsEligibilityCheckUrl;
        this.restTemplate = restTemplate;
        this.isEligibilityMSAPIEnabled = isEligibilityMSAPIEnabled;
        this.om = om;
        this.ntbservices_api_key = ntbservices_api_key;
        this.ntbservices_auth_key = ntbservices_auth_key;
        this.ntbservices_internal_url = ntbservices_internal_url;
        this.paymentOpsPropertyManager = paymentOpsPropertyManager;
        this.eligibilityApiKey = eligibilityApiKey;
        this.eligibilityAuthKey = eligibilityAuthKey;
        this.paymentOpsRestTemplate = commonPropertiesManager.getRestTemplate();
    }

    public TransactionResponse createTransaction(String merchant, PaymentTransactionRequest request) {
        HttpEntity<TransactionRequest> entity = new HttpEntity<>(request);
        try {
            LOGGER.info("Sending request to initiate transaction for merchant: {} and order id: {}", merchant,
                    request.getMerchantInfo().getMerchantOrderId());
            LOGGER.info("createTransaction, mobile:{}, email:{}", request.getConsumerInfo().getMobile(),
                    request.getConsumerInfo().getEmail());
            String url = PAYMENT_URL + "merchant/" + merchant + "/transactions";
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, TransactionResponse.class);
            LOGGER.info("Received response of initiate transaction for merchant: {} and order id: {}", merchant,
                    request.getMerchantInfo().getMerchantOrderId());
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred while initiating transaction for merchant: " + merchant +
                    " and order id: " + request.getMerchantInfo().getMerchantOrderId(), e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("code")) {
                String code = Util.handleServiceFailureResp(e.getResponseBodyAsString(), "code");
                if (StringUtils.hasText(code)) {
                    throw new FreewayCustomException(Integer.parseInt(code),
                            Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
                }
            }
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred while initiating transaction for merchant: " + merchant +
                    " and order id: " + request.getMerchantInfo().getMerchantOrderId(), e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while initiating transaction for merchant: " + merchant +
                    " and order id: " + request.getMerchantInfo().getMerchantOrderId(), e);
            if (e.getCause() instanceof SocketTimeoutException) {
                LOGGER.error("ResourceAccessException is instanceof SocketTimeoutException for merchant: " + merchant +
                        " and order id: " + request.getMerchantInfo().getMerchantOrderId());
            }
        } catch (Exception e) {
            LOGGER.error(
                    "Exception occurred while initiating transaction for merchant: " + merchant + " and order id: " +
                            request.getMerchantInfo().getMerchantOrderId(), e);
            throw new FreewayException("Something went wrong!");
        }
        throw new FreewayException("Bad Request.");
    }

    public List<EligibilityResponse> getEligibilityCheck(List<PaymentProviderEnum> providers, Float amount,
                                                         String mobile, String partner) {
        try {
            String providersList = getProvidersString(providers);
            if (BooleanUtils.isTrue(isEligibilityMSAPIEnabled)) {
                String url = (isEMSCallViaDiscovery ? emsDiscoveryBaseUrl : emsAPIBaseUrl) + emsEligibilityCheckUrl;
                EligibilityRequest eligibilityRequest =
                        EligibilityRequest.builder().partnerCode(partner).mobile(mobile).amount(amount).providers(providers).build();
                HttpEntity<EligibilityRequest> httpEntity = new HttpEntity<>(eligibilityRequest, populateHeaders());
                LOGGER.info("Sending request to new eligibility ms for mobile: {} on url: {} with request: {}", mobile,
                        url, om.writeValueAsString(eligibilityRequest));

                ResponseEntity<EligibilityMSResponse> response;
                if (isEMSCallViaDiscovery) {
                    response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, EligibilityMSResponse.class);
                } else {
                    RestTemplate rt = new RestTemplate();
                    response = rt.exchange(url, HttpMethod.POST, httpEntity, EligibilityMSResponse.class);
                }
                LOGGER.info("Response received from new eligibility ms for mobile: {} as: {}", mobile, response);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody().getCode() == 0) {
                    return response.getBody().getEligibilities();
                }
            } else {
                String url =
                        PAYMENT_URL + "/eligibilities?providers=" + providersList + "&amount=" + amount + "&mobile=" +
                                mobile + "&partner="+partner;
                ResponseEntity<List<EligibilityResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EligibilityResponse>>() {
                        });
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
            }

        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting eligibilities for mobile: " + mobile, e);
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getEligibilityCheck : Bad Request.");
    }

    private String getProvidersString(List<PaymentProviderEnum> providers) {
        if (!CollectionUtils.isEmpty(providers)) {
            StringBuilder providersList = new StringBuilder();
            for (PaymentProviderEnum provider : providers) {
                providersList.append(provider.getDisplayName());
                providersList.append(",");
            }
            return providersList.toString();
        }
        throw new FreewayCustomException(TransactionCode.FAILED_220);
    }

    public TransactionResponse getTransactionByQR(String consumerId, String consumerMobile, String transactionQr) {
        try {
            String url = PAYMENT_URL + "consumer/" + consumerId + "/transactions?mobile=" + consumerMobile + "&qid=" +
                    transactionQr;
            ResponseEntity<TransactionResponse> response = restTemplate.getForEntity(url, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public TransactionResponse getTransactionById(String transactionId) {
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId;
            ResponseEntity<TransactionResponse> response = restTemplate.getForEntity(url, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public TransactionResponse getNtbTransactionById(String transactionId) {
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId;
            ResponseEntity<TransactionResponse> response = restTemplate.getForEntity(url, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

    public TransactionResponse getTransactionByMerchantIdAndOrderId(String merchantId, String orderId) {
        try {
            String url = PAYMENT_URL + "merchant/" + merchantId + "/orderId/" + orderId;
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Something went wrong!", "merchant", merchantId);
    }



    public List<TransactionResponse> searchByMerchantId(String merchantId, String status) {
        return this.search(merchantId, "", "", status);
    }

    public List<TransactionResponse> searchByMerchantIdAndConsumerMobile(String merchantId, String consumerMobile) {
        return this.search(merchantId, "", consumerMobile, "");
    }

    public List<TransactionResponse> searchByConsumerId(String consumerId) {

        return this.search("", consumerId, "", "");
    }

    public List<TransactionResponse> searchByConsumerMobile(String consumerMobile) {
        return this.search("", "", consumerMobile, "");
    }

    private List<TransactionResponse> search(String merchantId, String consumerId, String consumerMobile,
                                             String status) {
        try {
            String url =
                    PAYMENT_URL + "search?merchantId=" + merchantId + "&consumerId=" + consumerId + "&consumerMobile=" +
                            consumerMobile + "&status=" + status;
            ResponseEntity<List<TransactionResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TransactionResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", "merchant", merchantId);
        }
        throw new FreewayException("getTransactionByQR : Bad Request", "merchant", merchantId);
    }

    public PaymentProviderTransactionResponse processPayment(String transactionId, PaymentRequest request) {
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, populateHeaders());
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId;
            ResponseEntity<PaymentProviderTransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, PaymentProviderTransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: for transaction id: " + transactionId,
                    e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("code")) {
                String code = Util.handleServiceFailureResp(e.getResponseBodyAsString(), "code");
                if (StringUtils.hasText(code)) {
                    throw new FreewayCustomException(Integer.parseInt(code),
                            Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
                }
            }
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: for transaction id: " + transactionId,
                    e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api for transaction id: " + transactionId, e);
            throw new FreewayException("Something went wrong", "transaction", transactionId);
        }
        throw new FreewayException("Bad Request.", "transaction", transactionId);
    }

    public TransactionResponse processRefund(String paymentTxnId, RefundTransactionRequest refundTxnReq) {
        HttpEntity<RefundTransactionRequest> entity = new HttpEntity<>(refundTxnReq);
        try {
            String url = PAYMENT_URL + "transactions/" + paymentTxnId + "/refunds";
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", "transaction", paymentTxnId);
        }
        throw new FreewayException("Bad Request.", "transaction", paymentTxnId);
    }


    public TransactionResponse processNtbRefund(String paymentTxnId, RefundTransactionRequest refundTxnReq) {
        HttpEntity<RefundTransactionRequest> entity = new HttpEntity<>(refundTxnReq);
        try {
            String url = PAYMENT_URL + "transactions/" + paymentTxnId + "/refunds";
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message")));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling ntb refund api api: ", e);
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
        }
        throw new FreewayException("Bad Request.", "transaction", paymentTxnId);
    }

    public PaymentProviderTransactionResponse processPatch(String paymentTxnId, Map<String, String> params) {
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params);
        try {
            String url = PAYMENT_URL + "transactions/" + paymentTxnId;
            LOGGER.info("Sending patch request on URL: {} with params: {} for transactionId: {}", url, params,
                    paymentTxnId);
            ResponseEntity<PaymentProviderTransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, entity, PaymentProviderTransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                PaymentProviderTransactionResponse pptr = response.getBody();
                LOGGER.info("Patch response for transactionId: {} is: {}", paymentTxnId, pptr);
                return pptr;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("code")) {
                String code = Util.handleServiceFailureResp(e.getResponseBodyAsString(), "code");
                if (StringUtils.hasText(code)) {
                    LOGGER.error("Throwing ");
                    throw new FreewayCustomException(Integer.parseInt(code),
                            Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
                }
            }
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", "transaction", paymentTxnId);
        }
        throw new FreewayException("Bad Request.", "transaction", paymentTxnId);
    }

    public List<PaymentProviderEnum> getProviders(String merchantId) {
        try {
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/merchant/" + merchantId + "/providers";
            LOGGER.info("Url:{}", url);
            ResponseEntity<List<PaymentProviderEnum>> response = paymentOpsRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<List<PaymentProviderEnum>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getProviders : Bad Request.", "merchant", merchantId);
    }

    public PgConsumerPaymentRequest getPgConsumerRequest(String transactionId) {
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId + "/pgConsumerPaymentRequest";
            LOGGER.info("Sending request to get pg consumer request for transaction id: {} on url: {}", transactionId,
                    url);
            ResponseEntity<PgConsumerPaymentRequest> response =
                    restTemplate.getForEntity(url, PgConsumerPaymentRequest.class);
            LOGGER.info("Response received to get pg consumer request for transaction id: {}", transactionId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public SettlementResponse getSettlement(String merchant) {
        try {
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/merchant/" + merchant + "/settlements";
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            ResponseEntity<SettlementResponse> response = paymentOpsRestTemplate.exchange(url, HttpMethod.GET, httpEntity, SettlementResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getTransactionByQR : Bad Request");
    }

    public List<TransactionInvoiceResponse> getTransactionInvoices(String merchant) {
        try {
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/merchant/" + merchant + "/invoices";
            ResponseEntity<List<TransactionInvoiceResponse>> response = paymentOpsRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<List<TransactionInvoiceResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        return new ArrayList<>();
    }

    public TransactionInvoiceResponse getTransactionInvoice(String merchant, String iid) {
        try {
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/invoices/" + iid + "?merchantId=" + merchant;
            ResponseEntity<TransactionInvoiceResponse> response =
                    paymentOpsRestTemplate.exchange(url,HttpMethod.GET, httpEntity, TransactionInvoiceResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Transaction Invoice Not Found!");
    }

    public List<EligibilityResponse> getEligibilityWithCardDetails(String transactionId,
                                                                   CheckEligibilityRequest request) {
        try {
            String url = PAYMENT_URL + "/eligibilities/getByCard/" + transactionId;
            HttpEntity<CheckEligibilityRequest> httpEntity = new HttpEntity<>(request);
            ResponseEntity<List<EligibilityResponse>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                    new ParameterizedTypeReference<List<EligibilityResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred: ", e);
            throw new FreewayException(e.getMessage());
        }
        return null;
    }

    public TransactionResponse lastPayment(String consumer) {
        try {
            String url = PAYMENT_URL + "consumer/" + consumer + "/lastPayment";
            ResponseEntity<TransactionResponse> response = restTemplate.getForEntity(url, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred: ", e);
        }
        return null;
    }

    public Float findPendingCashback(String user) {
        try {
            String url = PAYMENT_URL + "consumer/" + user + "/pendingCashback";
            ResponseEntity<List<TransactionResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TransactionResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                List<TransactionResponse> responses = response.getBody();
                Float sum = 0.0f;
                if (null != responses) {
                    for (TransactionResponse r : responses) {
                        if (r.getCashbackCharges() > 0.0 && "pending".equals(r.getAdditionInfo().getCashbackStatus())) {
                            sum += r.getCashbackCharges();
                        }
                    }
                }
                return sum;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
        }
        return 0.0f;
    }

    public List<PaymentProviderInfo> getProvidersInfo(String merchantId) {
        try {
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/merchant/" + merchantId + "/providersInfo";
            ResponseEntity<List<PaymentProviderInfo>> response = paymentOpsRestTemplate.exchange(url, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<List<PaymentProviderInfo>>() {
                    });
            LOGGER.info("providers response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getProviders : Bad Request.");
    }

    public StoreUserTransaction getTransaction(String storeUserId,
                                               StoreUserTransactionStatusReq storeUserTransactionStatusReq) {
        try {
            String url = PAYMENT_URL + "transactions/storeUserTransaction/" + storeUserId;
            if (StringUtils.hasText(storeUserTransactionStatusReq.getTransFrom())) {
                url = url + "?startDate=" + storeUserTransactionStatusReq.getTransFrom();
                if (StringUtils.hasText(storeUserTransactionStatusReq.getTransTo())) {
                    url = url + "&endDate=" + storeUserTransactionStatusReq.getTransTo();
                }
            }
            if (StringUtils.hasText(storeUserTransactionStatusReq.getStatus())) {
                url += StringUtils.hasText(storeUserTransactionStatusReq.getTransFrom()) ? "&status=" : "?status=";
                url += storeUserTransactionStatusReq.getStatus();
            }
            ResponseEntity<StoreUserTransaction> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, StoreUserTransaction.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred: ", e);
        }
        return null;
    }

    public List<TransactionResponse> pendingTransaction(String consumer, Integer limit, Integer offset) {
        try {
            String url = PAYMENT_URL + "consumer/" + consumer + "/pendingTransaction?" + "limit=" + limit + "&offset=" +
                    offset;
            ResponseEntity<List<TransactionResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TransactionResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
        }
        return null;
    }

    public List<TransactionResponse> findConsumerCashback(String user, Integer limit, Integer offset) {
        try {
            String url = PAYMENT_URL + "consumer/" + user + "/pendingCashback";
            if (null != limit && null != offset) {
                url = url + "?limit=" + limit + "&offset=" + offset;
            }
            ResponseEntity<List<TransactionResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<TransactionResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                List<TransactionResponse> responses = response.getBody();
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
        }
        return new ArrayList<>();
    }

    public RefundPayoutResponse createRefundPayout(String merchantId, RefundPayoutRequest request) {
        try {
            String url = PAYMENT_URL + "merchant/" + merchantId + "/refundPayouts";
            HttpEntity<RefundPayoutRequest> httpEntity = new HttpEntity<>(request, populateHeaders());
            ResponseEntity<RefundPayoutResponse> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                    new ParameterizedTypeReference<RefundPayoutResponse>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error(
                    "HttpClientErrorException occurred while creating refund payout: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error(
                    "HttpServerErrorException occurred while creating refund payout: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating refund payout: ", e);
        }
        return RefundPayoutResponse.builder()
                .statusCode(TransactionCode.FAILED_20.getCode())
                .status(TransactionCode.FAILED_20.getStatus())
                .statusMessage(TransactionCode.FAILED_20.getStatusMsg())
                .build();
    }

    public HttpHeaders populateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public List<TransactionResponse> searchTransactions(String source, String sourceId,
                                                        TransactionSearchFilter transactionSearchFilter) {
        try {
            String url = PAYMENT_BASE_URL + "/api/v1/search?source=" + source + "&sourceId=" + sourceId;
            HttpEntity<TransactionSearchFilter> entity = new HttpEntity<>(transactionSearchFilter);
            LOGGER.info("Sending search transactions request from source: {} with source id: {} and params: {}", source,
                    sourceId, transactionSearchFilter);
            ResponseEntity<List<TransactionResponse>> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<List<TransactionResponse>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", source, sourceId);
        }
        throw new FreewayException("searchTransactions : Bad Request", source, sourceId);
    }

    public PaymentConfigInfo getPaymentConfig(String merchantId) {
        try {
            String url = PAYMENT_URL + "merchant/" + merchantId + "/paymentConfig";
            ResponseEntity<PaymentConfigInfo> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, null, PaymentConfigInfo.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getProviders : Bad Request.", "merchant", merchantId);

    }

    public DownPaymentConfigDto getDownPaymentConfig(String merchantId) {
        try {
            HttpHeaders headers = GetHeadersPaymentOps();
            HttpEntity httpEntity = new HttpEntity<>(headers);
            String url = paymentOpsPropertyManager.getPaymentOpsBaseUrl() + "/api/v1/merchant/" + merchantId + "/downPaymentConfig";
            ResponseEntity<DownPaymentConfigDto> responseEntity =
                    paymentOpsRestTemplate.exchange(url, HttpMethod.GET, httpEntity, DownPaymentConfigDto.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("getProviders : Bad Request.", "merchant", merchantId);

    }

    public BinDetailResponse getBinDetails(String bin) {
        try {
            String url = PAYMENT_URL + "cardbins/" + bin;
            ResponseEntity<BinDetailResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<BinDetailResponse>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling get bin details api: ", e);
        }
        return null;
    }

    public DeliveryOrderResp getDeliveryOrder(String merchantId, String orderIdOrpaymentTxnId) {
        try {
            String url =
                    PAYMENT_URL + "merchant/" + merchantId + "/transactions/" + orderIdOrpaymentTxnId + "/deliveryOrder";
            ResponseEntity<DeliveryOrderResp> response = restTemplate.getForEntity(url, DeliveryOrderResp.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling get bin details api: ", e);
        }
        return null;
    }

    public TransactionResponse updateTransactionConsumerProfile(String transactionId, ProfileRequest request) {
        HttpEntity<ProfileRequest> entity = new HttpEntity<>(request);
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId + "/consumerProfile";
            LOGGER.info("Request received to update transaction consumer info for transactionId: {} is: {}",
                    transactionId, request);
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            LOGGER.info("Response received to update transaction consumer info for transactionId: {} is: {}",
                    transactionId, response);
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            if (e.getResponseBodyAsString().contains("code")) {
                String code = Util.handleServiceFailureResp(e.getResponseBodyAsString(), "code");
                if (StringUtils.hasText(code)) {
                    throw new FreewayCustomException(Integer.parseInt(code),
                            Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
                }
            }
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Bad Request.");
    }

    public SerialNumberTxnsResponse getSerialNumberSuccessCount(String brandId, String serialNumber) {
        try {
            String url = PAYMENT_URL + "transactions/" + brandId + "/count?serialNumber=" + serialNumber;
            ResponseEntity<SerialNumberTxnsResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, SerialNumberTxnsResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info(String.format("Serial Number success transaction count: %s", response.getBody()));
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred while get " + "transaction count for serial number, " +
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred while get " + "transaction count for serial number, " +
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while occurred while get transaction count for serial number, ", e);
        }
        return new SerialNumberTxnsResponse(-1L);
    }

    public <T> ResponseEntity<T> generateAgreement(String consumerId, String transactionId,
                                                   HttpEntity<?> generateAgreementRequest, Class<T> responseType) {
        try {
            String url = PAYMENT_URL + "transactions/" + transactionId + "/agreement";
            LOGGER.info("Sending internal request to payment service to generate agreement for consumer id: {} and " +
                    "transaction id: {} with request params: {}", consumerId, transactionId, generateAgreementRequest);
            ResponseEntity<T> responseEntity =
                    restTemplate.exchange(url, HttpMethod.POST, generateAgreementRequest, responseType);
            LOGGER.info(
                    "Response received from payment service to generate agreement for consumer id: {} and transaction" +
                            " id: {} with response: {}", consumerId, transactionId, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred as: " + e.getResponseBodyAsString() +
                            " while generating agreement for consumer id: " + consumerId + " transaction id: " + transactionId,
                    e);
            if (e.getResponseBodyAsString().contains("code")) {
                parseClientOrServerExceptionAndThrowFreewayCustomException(e.getResponseBodyAsString());
            }
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred as: " + e.getResponseBodyAsString() +
                            " while generating agreement for consumer id: " + consumerId + " transaction id: " + transactionId,
                    e);
            if (e.getResponseBodyAsString().contains("code")) {
                parseClientOrServerExceptionAndThrowFreewayCustomException(e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while occurred while generating agreement for consumer id: " + consumerId +
                    " transaction id: " + transactionId, e);
        }
        throw new FreewayCustomException(TransactionCode.FAILED_189);
    }

    private void parseClientOrServerExceptionAndThrowFreewayCustomException(String errorResponseAsString) {
        LOGGER.error("Parsing internal exception and throwing again to client.");
        String code = Util.handleServiceFailureResp(errorResponseAsString, "code");
        if (StringUtils.hasText(code)) {
            throw new FreewayCustomException(Integer.parseInt(code),
                    Util.handleServiceFailureResp(errorResponseAsString, "message"));
        }
    }

    public <T> ResponseEntity<T> consumerTransactionConsent(String consumerId,
                                                            HttpEntity<?> consumerTransactionConsents,
                                                            Class<T> responseType) {
        try {
            String url = PAYMENT_URL + "/consents";
            LOGGER.info(
                    "Sending internal request to payment service to save consumer consent for consumer id: {} with " +
                            "request params: {}", consumerId, consumerTransactionConsents);
            ResponseEntity<T> responseEntity =
                    restTemplate.exchange(url, HttpMethod.POST, consumerTransactionConsents, responseType);
            LOGGER.info("Response received from payment service to save consumer consent for consumer id: {} with " +
                    "response: {}", consumerId, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred as: " + e.getResponseBodyAsString() +
                    " while saving consent for consumer id: " + consumerId, e);
            if (e.getResponseBodyAsString().contains("code")) {
                parseClientOrServerExceptionAndThrowFreewayCustomException(e.getResponseBodyAsString());
            }
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred as: " + e.getResponseBodyAsString() +
                    " while saving consent for consumer id: " + consumerId, e);
            if (e.getResponseBodyAsString().contains("code")) {
                parseClientOrServerExceptionAndThrowFreewayCustomException(e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while occurred while saving consent in payment service for consumer id: " +
                    consumerId, e);
        }
        throw new FreewayException("Something went wrong!");
    }

    public RefundInquiryResponse getRefundInquiryResponse(String paymentTxnId, String merchantId) {
        try {
            String url = PAYMENT_URL + "merchant/" + merchantId + "/transactions/" + paymentTxnId + "/refunds/inquiry";
            ResponseEntity<RefundInquiryResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, RefundInquiryResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", "transaction", paymentTxnId);
        }
        throw new FreewayException("Bad Request.", "transaction", paymentTxnId);
    }

    public TransactionResponse getPgPaymentOptionsOnEligibilityV3(TransactionResponse tr) {
        try {
            String url = PAYMENT_INTERNAL_URL + "transactions/" + tr.getTxnId() + "/payment/options";
            ResponseEntity<TransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, TransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
            throw new FreewayException("Something went wrong", "transaction", tr.getTxnId());
        }
        throw new FreewayException("Bad Request.", "transaction", tr.getTxnId());
    }

    public PgExpireTransactionResponse expireTransaction(MerchantUser merchantUser,
                                                         PgExpireTransactionRequest request) {
        if (Util.isNotNull(request.getTransactionId())) {
            String transactionId = request.getTransactionId();
            TransactionResponse txnResponse = getTransactionById(transactionId);
            String txnCurrentStatus = txnResponse.getStatus();
            if (Util.isNotNull(request.getCardType()) && Util.isNotNull(request.getTransactionStatus())) {
                if (CardTypeEnum.NTB.getCardType().equals(request.getCardType()) &&
                        "processing".equals(txnCurrentStatus)) {
                    String url = ntbservices_internal_url + "loan?actionType=expireLoanByTransactionId";
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                        headers.add("X-API-KEY", ntbservices_api_key);
                        headers.add("X-AUTH-KEY", ntbservices_auth_key);
                        HttpEntity<PgExpireTransactionRequest> entity = new HttpEntity<>(request, headers);
                        LOGGER.info("expire transaction with request: {} {}", url, entity);
                        ResponseEntity<PgExpireTransactionResponse> response =
                                restTemplate.exchange(url, HttpMethod.PATCH, entity, PgExpireTransactionResponse.class);
                        LOGGER.info("expire transaction response: {}", response);
                        if ((response.getStatusCode().is2xxSuccessful() ||
                                response.getStatusCode().is4xxClientError()) && Objects.nonNull(response.getBody())) {
                            return response.getBody();
                        }
                    } catch (HttpClientErrorException | HttpServerErrorException e) {
                        LOGGER.error("HttpClientErrorException occurred: ", e);
                    } catch (Exception e) {
                        LOGGER.error("HttpServerErrorException occurred: ", e);
                    }
                }
            }
            if (!StringUtils.isEmpty(txnCurrentStatus) && Util.isNotNull(request.getTransactionStatus())) {
                if ("initiated".equals(txnCurrentStatus)) {
                    String url = PAYMENT_INTERNAL_URL + "transactions/updateStatus";
                    try {
                        HttpHeaders httpHeaders = GetHeaders();
                        HttpEntity<PgExpireTransactionRequest> entity = new HttpEntity<>(request, httpHeaders);
                        LOGGER.info("expire transaction with request: {} {}", url, entity);
                        ResponseEntity<PgExpireTransactionResponse> response =
                                restTemplate.exchange(url, HttpMethod.POST, entity, PgExpireTransactionResponse.class);
                        LOGGER.info("expire transaction response: {}", response);
                        if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody()) &&
                                Objects.nonNull(response.getBody().getStatus())) {
                            if ("success".equals(response.getBody().getStatus()) ||
                                    "failed".equals(response.getBody().getStatus())) {
                                return response.getBody();
                            }
                        }
                    } catch (HttpClientErrorException | HttpServerErrorException e) {
                        LOGGER.error("HttpClientErrorException occurred: " + e);
                    } catch (Exception e) {
                        LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
                    }
                }
            }
        }
        throw new FreewayException("Something went wrong while expiring transaction!", "transaction",
                request.getTransactionId());
    }

    public PaymentOptionsResponse getPaymentOptions(PaymentOptionsRequest paymentOptionsRequest) {
        try {
            String url = PAYMENT_INTERNAL_URL + "transactions/payment/options";
            HttpEntity<PaymentOptionsRequest> entity = new HttpEntity<>(paymentOptionsRequest, GetHeaders());
            ResponseEntity<PaymentOptionsResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, PaymentOptionsResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api: ", e);
        }
        return null;
    }

    public UpdateTransactionResponse updateTransaction(String transactionId, UpdateTransactionRequest request) {
        try {
            String url = PAYMENT_BASE_URL + "/internal/api/v2/transactions/" + transactionId;
            HttpEntity<UpdateTransactionRequest> entity = new HttpEntity<>(request, GetHeaders());
            ResponseEntity<UpdateTransactionResponse> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, entity, UpdateTransactionResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling patch api for serial number update: ", e);
        }
        return null;
    }

    public ProviderConfigResponse getProviderConfig(ProviderConfigRequest providerConfigRequest) {
        String url = emsDiscoveryBaseUrl + "/internal/api/v2/providerconfig/get";
        try {
            HttpHeaders httpHeaders = GetHeaders();
            HttpEntity<ProviderConfigRequest> entity = new HttpEntity<>(providerConfigRequest, getHeadersWithKeys());
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

    public PostPaymentResponse validateOtp(ValidateOtpRequest validateOtpRequest, String paymentTxnId) {
        String url = PAYMENT_URL + "transactions/" + paymentTxnId + "/validateOTP";
        try {
            HttpHeaders httpHeaders = GetHeaders();
            HttpEntity<ValidateOtpRequest> entity = new HttpEntity<>(validateOtpRequest, httpHeaders);
            LOGGER.info("validateOtp for transactionId: {}", paymentTxnId);
            ResponseEntity<PostPaymentResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, PostPaymentResponse.class);
            LOGGER.info("validateOtp for transactionId: {} and response: {}", paymentTxnId, response);
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

    public PriceResponse getPgPaymentOptionByPG(String transactionId, String cardType, String bankCode, Integer tenure,
                                                Integer advanceEmiTenure, Float downpaymentAmount) {
        try {

            String url = PAYMENT_URL + "transactions/" + transactionId + "/payment/offers?";
            if (StringUtils.hasText(cardType)) {
                url += "cardType=" + cardType;
            }
            if (StringUtils.hasText(bankCode)) {
                url += "&bankCode=" + bankCode;
            }
            if (Objects.nonNull(tenure)) {
                url += "&tenure=" + tenure;
            }
            if (Objects.nonNull(advanceEmiTenure)) {
                url += "&advanceEmiTenure=" + advanceEmiTenure;
            }
            if (Objects.nonNull(downpaymentAmount)) {
                url += "&downpaymentAmount=" + downpaymentAmount;
            }
            LOGGER.info("Sending request to payment to get price response for transaciton id: {} on url: {}",
                    transactionId, url);
            ResponseEntity<PriceResponse> response = restTemplate.getForEntity(url, PriceResponse.class);
            LOGGER.info("Response received: {} price response from payment for transaction id: {}", response,
                    transactionId);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error(
                    "HttpClientErrorException occurred while receiving price response from payment for transaction " +
                            "id: {} with error: {}", transactionId, e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error(
                    "HttpServerErrorException occurred while receiving price response from payment for transaction " +
                            "id: {} with error: {} ", transactionId, e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while receiving price response from payment for transaction id: {} with " +
                    "error: {} ", transactionId, e);
            throw new FreewayException("Something went wrong", "transaction", transactionId);
        }
        throw new FreewayException("Bad Request.", "transaction", transactionId);
    }

    private HttpHeaders GetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders getHeadersWithKeys() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", eligibilityApiKey);
        headers.add("X-AUTH-KEY", eligibilityAuthKey);
        return headers;
    }

    private HttpHeaders GetHeadersPaymentOps() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", paymentOpsPropertyManager.getPaymentOpsApiKeys());
        headers.add("X-AUTH-KEY", paymentOpsPropertyManager.getPaymentOpsAuthKeys());
        return headers;
    }
}
