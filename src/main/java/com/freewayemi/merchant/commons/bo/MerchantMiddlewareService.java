package com.freewayemi.merchant.commons.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.CreateProductSkuRequest;
import com.freewayemi.merchant.commons.dto.ProductSkuResponse;
import com.freewayemi.merchant.commons.dto.UpdateProductSkuRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class MerchantMiddlewareService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantMiddlewareService.class);
    private static final String MERCHANT_MIDDLEWARE_URL = "http://merchantmiddlewarems/merchantmiddleware/api/v1";
    private final RestTemplate restTemplate;
    private final ObjectMapper om;
    public static final Integer CLAIMED_STATUS = 10;
    public static final Integer UNCLAIMED_STATUS = 11;
    public static final Integer VALID_STATUS = 0;
    public static final Integer BRAND_SERVER_ERROR = 19;

    @Autowired
    public MerchantMiddlewareService(RestTemplate restTemplate, ObjectMapper om) {
        this.restTemplate = restTemplate;
        this.om = om;
    }

    public List<Double> handleListWithNullValues(List<Double> data) {
        if(null != data && data.size() >=2) {
            if(null != data.get(0) && null != data.get(1)) {
                return data;
            }
        }
        return new ArrayList<>();
    }

    public ProductSkuResponse createProductSku(String merchantId, String productId, String serialNumber,
                                               String modelNo, String consumerId, String consumerMobile, String brand,
                                               String stage, String transactionId, String source, String brandId,
                                               String gst, String productName, Float amount,
                                               Address address, Integer tenure, Boolean isSerialNumberRequired, String schemeId) {
        try {
            if(null != address) {
                address.setCoordinates(handleListWithNullValues(address.getCoordinates()));
                address.setReverseCoordinates(handleListWithNullValues(address.getReverseCoordinates()));
                address.setReverseUserCoordinates(handleListWithNullValues(address.getReverseUserCoordinates()));
                address.setUserCoordinates(handleListWithNullValues(address.getUserCoordinates()));
            }
            CreateProductSkuRequest request = CreateProductSkuRequest.builder()
                    .merchantId(merchantId)
                    .productId(productId)
                    .serialNumber(serialNumber)
                    .modelNumber(modelNo)
                    .consumerId(consumerId)
                    .consumerMobile(consumerMobile)
                    .brand(brand != null ? brand.replaceAll(" ", "").toUpperCase() : "")
                    .stage(stage)
                    .transactionId(transactionId)
                    .source(source)
                    .brandId(brandId)
                    .gst(gst)
                    .isSerialNoRequired(isSerialNumberRequired)
                    .productName(productName)
                    .amount(amount)
                    .address(address)
                    .offerTenure(tenure)
                    .schemeId(schemeId)
                    .build();
            HttpEntity<CreateProductSkuRequest> entity = new HttpEntity<>(request);
            String url = MERCHANT_MIDDLEWARE_URL + "/productSkus";
            LOGGER.info("Sending create ProductSku request on url: {} with request: {} for product id: {} for transaction id: {}", url,
                    request, productId, transactionId);
            ResponseEntity<ProductSkuResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    ProductSkuResponse.class);
            LOGGER.info("Response received for product id: {} with entity {} for transaction id: {}", productId, responseEntity, transactionId);
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
        return null;
    }

    /*
        use case 1 : called by validate serial number in qr flow.
        use case 2 : called by transaction prepayment.
     */
    public ProductSkuResponse updateProductSku(String merchantId, String productId, String serialNumber,
                                               String modelNumber, String consumerId, String consumerMobile,
                                               String brand, String status, String stage, String productSkuId,
                                               String transactionId, Integer tenure) {
        try {
            UpdateProductSkuRequest request = UpdateProductSkuRequest.builder()
                    .merchantId(merchantId)
                    .productId(productId)
                    .serialNumber(serialNumber)
                    .modelNumber(modelNumber)
                    .consumerId(consumerId)
                    .consumerMobile(consumerMobile)
                    .brand(brand != null ? brand.replaceAll(" ", "").toUpperCase() : "")
                    .status(status)
                    .stage(stage)
                    .transactionId(transactionId)
                    .offerTenure(tenure)
                    .build();
            HttpEntity<UpdateProductSkuRequest> entity = new HttpEntity<>(request);
            String url = MERCHANT_MIDDLEWARE_URL + "/productSkus/" + productSkuId;
            LOGGER.info("Sending update ProductSku request on url: {} with request: {} for product id: {} for transaction id: {}", url, request,
                    productId, transactionId);
            ResponseEntity<ProductSkuResponse> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity,
                    ProductSkuResponse.class);
            LOGGER.info("Response received for product id: {} with entity {} for transaction id: {}", productId, responseEntity, transactionId);
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
        return null;
    }

    public ProductSkuResponse updateProductSkuPostPayment(String merchantId, String productId, String serialNumber,
                                                          String modelNumber, String consumerId, String consumerMobile,
                                                          String brand, String status, String stage,
                                                          String productSkuId, String transactionId) {
        try {
            UpdateProductSkuRequest request = UpdateProductSkuRequest.builder()
                    .merchantId(merchantId)
                    .productId(productId)
                    .serialNumber(serialNumber)
                    .modelNumber(modelNumber)
                    .consumerId(consumerId)
                    .consumerMobile(consumerMobile)
                    .brand(brand != null ? brand.replaceAll(" ", "").toUpperCase() : "")
                    .status(status)
                    .stage(stage)
                    .transactionId(transactionId)
                    .build();
            HttpEntity<UpdateProductSkuRequest> entity = new HttpEntity<>(request);
            String url = MERCHANT_MIDDLEWARE_URL + "/productSkus/" + productSkuId;
            LOGGER.info("Sending update ProductSku request on url: {} with request: {} for product id: {} for transaction id: {}", url,
                    request, productId, transactionId);
            ResponseEntity<ProductSkuResponse> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity,
                    ProductSkuResponse.class);
            LOGGER.info("Response received for product id: {} with entity {} for transaction id: {}", productId, responseEntity, transactionId);
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
        return null;
    }

    public ProductSkuResponse asyncVerifyAndClaim(UpdateProductSkuRequest request, String productSkuId) {
        try {
            HttpEntity<UpdateProductSkuRequest> entity = new HttpEntity<>(request);
            String url = MERCHANT_MIDDLEWARE_URL + "/productSkus/" + productSkuId + "/asyncVerifyAndClaim";
            LOGGER.info("Sending ProductSku async claim request on url: {} with request: {} for product id: {}" +
                            " for transaction id: {}", url, om.writeValueAsString(request), request.getProductId(),
                    request.getTransactionId());
            ResponseEntity<ProductSkuResponse> responseEntity =
                    restTemplate.exchange(url, HttpMethod.PUT, entity, ProductSkuResponse.class);
            LOGGER.info("Response received for product id: {} with entity {} for transaction id: {}",
                    request.getProductId(), responseEntity, request.getTransactionId());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("ProductSku async claim response for transaction id: {} is: {}", request.getTransactionId(),
                        om.writeValueAsString(responseEntity.getBody()));
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: " + e.getResponseBodyAsString());
            throw new FreewayException(Util.handleServiceFailureResp(e.getResponseBodyAsString(), "message"));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while claiming async product sku for transaction id: "
                    + request.getTransactionId(), e);
            throw new FreewayException(e.getMessage());
        }
        return null;
    }
}
