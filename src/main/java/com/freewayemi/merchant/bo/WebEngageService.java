package com.freewayemi.merchant.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.dto.webengage.*;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.utils.CommonRestTemplateClient;
import com.freewayemi.merchant.utils.ConsumerEventTrackerConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebEngageService {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(WebEngageService.class);
    private final String webEngageBaseUrl;
    private final String webEngageMerchantLicenseCode;
    private final String webEngageMerchantAuthToken;

    @Autowired
    public WebEngageService(@Value("${webengage.base.url}") String webEngageBaseUrl,
                            @Value("${webengage.merchant.license.code}") String webEngageMerchantLicenseCode,
                            @Value("${webengage.merchant.auth.token}") String webEngageMerchantAuthToken) {
        this.webEngageBaseUrl = webEngageBaseUrl;
        this.webEngageMerchantLicenseCode = webEngageMerchantLicenseCode;
        this.webEngageMerchantAuthToken = webEngageMerchantAuthToken;
    }

    public void sendMerchantUserProfile(WebengageMerchantProfileRequest webengageMerchantProfileRequest, String url, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<WebengageMerchantProfileRequest> httpEntity = new HttpEntity<>(webengageMerchantProfileRequest, headers);
            LOGGER.info("Sending webengage track request with url : {},  params: {}", url, objectMapper.writeValueAsString(webengageMerchantProfileRequest));
            ResponseEntity<WebEngageResponse> responseEntity = CommonRestTemplateClient.getRestTemplate(15, 15)
                    .postForEntity(url, httpEntity, WebEngageResponse.class);
            LOGGER.info("Received webengage response with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Response  is: {}", objectMapper.writeValueAsString(responseEntity.getBody()));
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling webengage tracking api:", e);
        }
    }
    public void sendUserProfile(WebengageUserProfileRequest webengageUserProfileRequest, String url, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<WebengageUserProfileRequest> httpEntity = new HttpEntity<>(webengageUserProfileRequest, headers);
            LOGGER.info("Sending webengage track request with url : {},  params: {}", url, objectMapper.writeValueAsString(webengageUserProfileRequest));
            ResponseEntity<WebEngageResponse> responseEntity = CommonRestTemplateClient.getRestTemplate(15, 15)
                    .postForEntity(url, httpEntity, WebEngageResponse.class);
            LOGGER.info("Received webengage response with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Response  is: {}", objectMapper.writeValueAsString(responseEntity.getBody()));
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling webengage tracking api:", e);
        }
    }

    public void sendEvents(WebengageEventRequest webengageEventRequest, String url, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.add("Authorization", "Bearer " + authToken);
            HttpEntity<WebengageEventRequest> httpEntity = new HttpEntity<>(webengageEventRequest, headers);
            LOGGER.info("Sending webengage track request with url : {},  params: {}", url, objectMapper.writeValueAsString(webengageEventRequest));
            ResponseEntity<WebEngageResponse> responseEntity = CommonRestTemplateClient.getRestTemplate(15, 15)
                    .postForEntity(url, httpEntity, WebEngageResponse.class);
            LOGGER.info("Received webengage response with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Response  is: {}", objectMapper.writeValueAsString(responseEntity.getBody()));
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}",
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while calling webengage tracking api:", e);
        }
    }


    @Async
    public void sendWebEngageMerchantProps(MerchantUser merchantResponse) {
        LOGGER.info("Request received to send merchant profile to merchant-webengage with mobile  : {}", merchantResponse.getMobile());
        try {
            WebengageMerchantProfileRequest webengageMerchantProfileRequest = new WebengageMerchantProfileRequest(
                    merchantResponse.getMobile(), merchantResponse.getFirstName(), merchantResponse.getLastName(), merchantResponse.getEmail()
                    , null, null, ConsumerEventTrackerConstant.COUNTRY_CODE + merchantResponse.getMobile(), true,
                    WebengageMerchantProfileAttribute.builder()
                            .city(null != merchantResponse.getAddress() ? merchantResponse.getAddress().getCity() : "")
                            .state(null != merchantResponse.getAddress() ? merchantResponse.getAddress().getState() : "")
                            .address(null != merchantResponse.getAddress() ? merchantResponse.getAddress().toString() : "")
                            .notification_enabled(true)
                            .approval_date(null != merchantResponse.getApprovedDate() ?  DateUtil.getInstantDateInISTWithPattern(ConsumerEventTrackerConstant.WEBENGAGE_DATE_FORMAT, merchantResponse.getApprovedDate()) + ConsumerEventTrackerConstant.WEBENGAGE_DATE_FORMAT_HHMM : null)
                            .QR_status(merchantResponse.getQrCode())
                            .mobile_app_installed(null != merchantResponse.getAppInstalledDate())
                            .display_id(merchantResponse.getDisplayId())
                            .merchant_id(merchantResponse.getId().toString())
                            .merchant_registered_name(merchantResponse.getBusinessName())
                            .merchant_display_name(merchantResponse.getShopName())
                            .store_classification(null != merchantResponse.getParams() ? merchantResponse.getParams().getStoreClassification() : "")
                            .brand_list(null != merchantResponse.getParams() ? null != merchantResponse.getParams().getBrandIds() ?  merchantResponse.getParams().getBrandIds().toString() : merchantResponse.getParams().getBrand() : "")
                            .merchant_status( merchantResponse.getStatus())
                            .secondary_owner_portal_user_id("")
                            .store_category(merchantResponse.getCategory())
                            .store_sub_category(merchantResponse.getSubCategory())
                            .ownership(merchantResponse.getOwnership())
                            .merchant_registration_source(merchantResponse.getSource())
                            .merchant_registration_date(null != merchantResponse.getCreatedDate() ? DateUtil.getInstantDateInISTWithPattern(ConsumerEventTrackerConstant.WEBENGAGE_DATE_FORMAT, merchantResponse.getCreatedDate())+ConsumerEventTrackerConstant.WEBENGAGE_DATE_FORMAT_HHMM : null)
                            .type(merchantResponse.getType())
                            .build());
            sendMerchantUserProfile(webengageMerchantProfileRequest, webEngageBaseUrl + webEngageMerchantLicenseCode + ConsumerEventTrackerConstant.WEBENGAGE_USER_PROPS_URL, webEngageMerchantAuthToken);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating merchant-webengage tracking payload:", e);
        }
    }

    @Async
    public void sendWebEngageStoreUserProps(AdminAuthUser adminAuthUser) {
        LOGGER.info("Request received to send storeUser profile to merchant-webengage with mobile  : {}", adminAuthUser.getMobile());
        try {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("city", null != adminAuthUser.getCity() ? adminAuthUser.getCity() : "" );
            attributes.put("merchant_id", null != adminAuthUser.getMerchantId() ? adminAuthUser.getMerchantId() : "" );
            attributes.put("mobile_app_installed", null != adminAuthUser.getAppInstalledDate() ? "true": "false");
            attributes.put("name", null != adminAuthUser.getName() ? adminAuthUser.getName() : "" );
            attributes.put("dob", null != adminAuthUser.getDOB() ? adminAuthUser.getDOB() : "" );
            attributes.put("status", null != adminAuthUser.getStatus() ? adminAuthUser.getStatus() : "" );
            attributes.put("role", null != adminAuthUser.getRole() ? adminAuthUser.getRole() : "" );
            attributes.put("source", null != adminAuthUser.getSource() ? adminAuthUser.getSource() : "" );
            WebengageUserProfileRequest webengageUserProfileRequest = new WebengageUserProfileRequest(
                    adminAuthUser.getMobile(), adminAuthUser.getName(), "", adminAuthUser.getLogin()
                    , null, null, ConsumerEventTrackerConstant.COUNTRY_CODE + adminAuthUser.getMobile(), true, attributes);
            sendUserProfile(webengageUserProfileRequest, webEngageBaseUrl + webEngageMerchantLicenseCode + ConsumerEventTrackerConstant.WEBENGAGE_USER_PROPS_URL, webEngageMerchantAuthToken);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating merchant-webengage tracking payload:", e);
        }
    }
}
