package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.sales.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class SalesMSService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesMSService.class);

    private final RestTemplate restTemplate;
    private final String salesUrl;
    private final String sales_api_key;
    private final String sales_auth_key;


    @Autowired
    public SalesMSService(RestTemplate restTemplate,
                          @Value("${payment.sales.url}") String salesUrl,
                          @Value("${sales.api.key}") String sales_api_key,
                          @Value("${sales.auth.key}") String sales_auth_key) {
        this.salesUrl = salesUrl;
        this.sales_api_key=sales_api_key;
        this.sales_auth_key=sales_auth_key;
        this.restTemplate = restTemplate;
    }

    public MerchantTransactionVolumeResponse getMerchantTransactions(MerchantTransactionVolumeRequest merchantTransactionVolumeRequest) {
        String url = salesUrl + "/merchant/getMerchantTransactions?";
        try {
            HttpHeaders httpHeaders = GetSalesAPIHeaders();
            if (!StringUtils.isEmpty(merchantTransactionVolumeRequest.getMerchantId()) &&
                    !StringUtils.isEmpty(merchantTransactionVolumeRequest.getYear()) &&
                    !StringUtils.isEmpty(merchantTransactionVolumeRequest.getMonth())) {
                url = url + "merchantId=" + Util.encodeValue(merchantTransactionVolumeRequest.getMerchantId())
                        + "&month=" + Util.encodeValue(merchantTransactionVolumeRequest.getMonth())
                        + "&year=" + Util.encodeValue(merchantTransactionVolumeRequest.getYear());
            }
            HttpEntity<MerchantTransactionVolumeRequest> entity = new HttpEntity<>(httpHeaders);
            LOGGER.info("getMerchantTransactions request: {} {}", url, entity);
            ResponseEntity<MerchantTransactionVolumeResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, MerchantTransactionVolumeResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e);
        } catch (Exception e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getMessage());
        }
        return null;
    }

    private HttpHeaders GetSalesAPIHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", sales_api_key);
        headers.add("X-AUTH-KEY", sales_auth_key);
        return headers;
    }

}
