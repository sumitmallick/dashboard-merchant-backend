package com.freewayemi.merchant.service;


import com.freewayemi.merchant.commons.dto.karza.GstAuthReq;
import com.freewayemi.merchant.dto.AddressResponse;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.FreewayValidationException;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthReq;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthResp;
import com.freewayemi.merchant.dto.gst.GstAuthResp;
import com.freewayemi.merchant.pojos.gst.GstDetailsRequest;
import com.freewayemi.merchant.pojos.gst.GstDetailsResponse;
import com.freewayemi.merchant.pojos.pan.PanDetailsRequest;
import com.freewayemi.merchant.pojos.pan.PanDetailsResponse;
import com.freewayemi.merchant.type.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Objects;

@Component
public class DigitalIdentityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DigitalIdentityService.class);

    private final String digitalIndentityUrl;
    private final String apiKey;
    private final String authKey;
    private final RestTemplate restTemplate;

    @Autowired
    public DigitalIdentityService(@Value("${digitalidentity.base.url}") String digitalIndentityUrl,
                                  @Value("${digitalidentity.api.key}") String apiKey, @Value("${digitalidentity.auth.key}") String authKey,CommonPropertiesManager commonPropertiesManager) {
        this.digitalIndentityUrl = digitalIndentityUrl;
        this.apiKey = apiKey;
        this.authKey = authKey;
        this.restTemplate=commonPropertiesManager.getRestTemplate();
    }

    public GstDetailsResponse getGstDetails(GstDetailsRequest gstDetailsRequest) {
        HttpHeaders headers = GetHeaders();
        try {
            HttpEntity<GstDetailsRequest> entity = new HttpEntity<>(gstDetailsRequest, headers);
            LOGGER.info("Sending gst request to surepass with params: {}", gstDetailsRequest);
            ResponseEntity<GstDetailsResponse> response =
                    restTemplate.exchange(digitalIndentityUrl + "/private/api/v1/all/details/gst", HttpMethod.POST, entity,
                            GstDetailsResponse.class);
            LOGGER.info("Received response for gst request from surepass with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (Objects.requireNonNull(response.getBody()).getCode() == 0) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getCode(), "GstValidation");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayValidationException("GST",
                    "The GST does not match the details in the system. Please check and add again");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public PanDetailsResponse getPANDetails(PanDetailsRequest panDetailsRequest) {
        HttpHeaders headers = GetHeaders();
        try {
            panDetailsRequest = PanDetailsRequest.builder().panNumber(panDetailsRequest.getPanNumber()).source(Source.MERCHANTMS).build();
            HttpEntity<PanDetailsRequest> entity = new HttpEntity<>(panDetailsRequest, headers);
            LOGGER.info("Sending pan request to surepass with params: {}", panDetailsRequest);
            ResponseEntity<PanDetailsResponse> response =
                    restTemplate.exchange(digitalIndentityUrl + "/private/api/v1/verify/pan", HttpMethod.POST, entity,
                            PanDetailsResponse.class);
            LOGGER.info("Received response for pan request from surepass with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (Objects.requireNonNull(response.getBody()).getCode() == 0) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getCode(), "PANValidation");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayValidationException("PAN",
                    "The PAN does not match the details in the system. Please check and add again");
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        return null;
    }

    public BankAccountAuthResp verifyAccount(BankAccountAuthReq bankAccountAuthReq) {
        HttpHeaders headers = GetHeaders();
        try {
            HttpEntity<BankAccountAuthReq> entity = new HttpEntity<>(bankAccountAuthReq, headers);
            LOGGER.info("Sending bank details request with params: {}", bankAccountAuthReq);
            ResponseEntity<BankAccountAuthResp> response =
                    restTemplate.exchange(digitalIndentityUrl + "/private/api/v1/verify/bankDetails", HttpMethod.POST, entity,
                            BankAccountAuthResp.class);
            LOGGER.info("Received response for account request with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (Objects.requireNonNull(response.getBody()).getCode() == 0) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getCode(), "AccountValidation");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayValidationException("Account",
                    "The Account does not match the details in the system. Please check and add again");
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        return null;
    }

    public GstAuthResp verifyGst(GstAuthReq gstAuthReq) {
        HttpHeaders headers = GetHeaders();
        try {
            HttpEntity<GstAuthReq> entity = new HttpEntity<>(gstAuthReq, headers);
            LOGGER.info("Sending gst request with params: {}", entity);
            ResponseEntity<GstAuthResp> response =
                    restTemplate.exchange(digitalIndentityUrl + "/private/api/v1/details/gst", HttpMethod.POST, entity,
                            GstAuthResp.class);
            LOGGER.info("Received response for gst request with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (Objects.requireNonNull(response.getBody()).getCode() == 0) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getCode(), "GstValidation");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.info("Received response for gst request with response: {}", e.getResponseBodyAsString());
            throw new FreewayValidationException("GST",
                    "The GST does not match the details in the system. Please check and add again");
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        return null;
    }

    public AddressResponse getPostalAddress(String pinCode){
        try {
            HttpHeaders httpHeaders = GetHeaders();
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            String url = digitalIndentityUrl + "/internal/api/v1/pincodes/" + pinCode;
            LOGGER.info("Request received to fetch city and state : {} {}", url, entity);
            ResponseEntity<AddressResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, AddressResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Response received from digital identity service pin code api url: {}, response: {}", url, response.getBody());
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred: {} for pin code: " + pinCode,
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred: {} for pin code: " + pinCode,
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling digital identity service to get postal address with error: ", e);
        }
        return null;
    }

    private HttpHeaders GetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("X-API-KEY", apiKey);
        headers.add("X-AUTH-KEY", authKey);
        return headers;
    }

    private void handleInternalStatusCode(Integer statusCode, String api) {
        switch (statusCode) {
            case 20:
                switch (api) {
                    case "GstValidation":
                        throw new FreewayException(
                                "The GST does not match the details in the system. Please check and add again");
                    case "PANValidation":
                        throw new FreewayException(
                                "The PAN does not match the details in the system. Please check and add again");
                    case "AccountValidation":
                        throw new FreewayException(
                                "The Account does not match the details in the system. Please check and add again");
                    default:
                        throw new FreewayException("Invalid Data");
                }
            default:
                throw new FreewayException("Invalid Data");
        }
    }

    private void handleStatusCodes(int statusCode) {
        switch (statusCode) {
            case 400:
                throw new FreewayCustomException(400, "Bad request");
            case 401:
                throw new FreewayCustomException(400, "Unauthorized Access");
            case 402:
                throw new FreewayCustomException(400, "Insufficient Credits");
            case 500:
                throw new FreewayCustomException(400, "Internal Server Error");
            case 503:
                throw new FreewayCustomException(400, "Source Unavailable");
            case 504:
                throw new FreewayCustomException(400, "Endpoint Request Timed Out");
        }

    }
}


