package com.freewayemi.merchant.commons.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.aadhaarmask.AadhaarMaskReqDTO;
import com.freewayemi.merchant.commons.dto.karza.*;
import com.freewayemi.merchant.commons.dto.ntb.*;
import com.freewayemi.merchant.commons.dto.ntbservices.KYCResponseDto;
import com.freewayemi.merchant.dto.sales.GSTDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NtbCoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NtbCoreService.class);

    private final String ntbCoreBaseUrl = "http://ntbcorems/ntbcore";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public NtbCoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ConsumerAccountAuthResponse pennyDrop(BankAccVerificationDto bankAccVerificationDto, String userId) {
        ConsumerAccountAuthResponse authResponse = null;
        try {
            ConsumerAccountAuthRequest authRequest = ConsumerAccountAuthRequest.builder()
                    .accountNumber(bankAccVerificationDto.getAccountNumber())
                    .ifsc(bankAccVerificationDto.getIfsc())
                    .source(bankAccVerificationDto.getSource())
                    .consumerId(bankAccVerificationDto.getConsumerId())
                    .merchantId(bankAccVerificationDto.getMerchantId())
                    .build();
            HttpEntity<ConsumerAccountAuthRequest> entity = new HttpEntity<>(authRequest, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/verifyAccount";
            LOGGER.info("Sending bank account verification request for user id: {} on url: {} with params: {}", userId, url, authRequest);
            ResponseEntity<ConsumerAccountAuthResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    ConsumerAccountAuthResponse.class);
            LOGGER.info("Response for bank account verification received for user id: {} with response entity: {}", userId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                authResponse = responseEntity.getBody();
                LOGGER.info("Response for bank account verification for user id: {} is: {}" + userId, authResponse);
            }
            if (responseEntity.getStatusCode().value() == 400) {
                authResponse = new ConsumerAccountAuthResponse();
                authResponse.setStatusCode("NOT_VERIFIED");
                authResponse.setStatusMessage("Not Verified");
                return authResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for user id: " + userId,
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for user id: " + userId,
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending bank verification request for user id: " + userId, e);
        }
        return authResponse;
    }

    public GstAuthResp verifyGst(GstAuthReq gstRequest) {
        GstAuthResp gstResponse = null;
        try {

            HttpEntity<GstAuthReq> entity = new HttpEntity<>(gstRequest, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/verifyGst";
            LOGGER.info("Sending gst verification request: {}, url: {}", gstRequest, url);
            ResponseEntity<GstAuthResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    GstAuthResp.class);
            LOGGER.info("Response for gst verification: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                gstResponse = responseEntity.getBody();
                LOGGER.info("Response body for gst verification: {}", gstResponse);
            }
            if (responseEntity.getStatusCode().value() == 400) {
                return new GstAuthResp("NOT_VERIFIED", "Not Verified", "", null);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending gst verification request, Exception: {}", e);
        }
        return gstResponse;
    }

    public PanAuthResp verifyPan(PanAuthReq panRequest) {
        PanAuthResp panResponse = null;
        try {

            HttpEntity<PanAuthReq> entity = new HttpEntity<>(panRequest, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/verifyPan";
            LOGGER.info("Sending pan verification request: {}, url: {}", panRequest, url);
            ResponseEntity<PanAuthResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    PanAuthResp.class);
            LOGGER.info("Response for pan verification: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                panResponse = responseEntity.getBody();
                LOGGER.info("Response body for pan verification: {}", panResponse);
            }
            if (responseEntity.getStatusCode().value() == 400) {
                return new PanAuthResp("NOT_VERIFIED", "Not Verified", "", "", null);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending gst verification request, Exception: {}", e);
        }
        LOGGER.info("Pan Response:{}",panResponse);
        return panResponse;
    }

    private HttpHeaders populateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public AadhaarMaskResponse aadhaarMask(String merchantId, String key, String source) {
        AadhaarMaskResponse aadhaarMaskResponse = null;
        try {
            AadhaarMaskReqDTO aadhaarMaskReq = AadhaarMaskReqDTO.builder()
                    .merchantId(merchantId).key(key).source(source).build();
            HttpEntity<AadhaarMaskReqDTO> entity = new HttpEntity<AadhaarMaskReqDTO>(aadhaarMaskReq, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/maskAadhaar";
            LOGGER.info("Sending aadhaar masking request for merchant id: {} on url: {} with params: {}", merchantId, url, aadhaarMaskReq);
            ResponseEntity<AadhaarMaskResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    AadhaarMaskResponse.class);
            LOGGER.info("Response for aadhaar masking received for merchant id: {} with response entity: {}", merchantId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                aadhaarMaskResponse = responseEntity.getBody();
                LOGGER.info("Response body for aadhaar masking for merchantId: {} is: {}", merchantId, aadhaarMaskResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for merchant id: " + merchantId,
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for merchant id: " + merchantId,
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while aadhaar masking request for merchant id:" + merchantId, e);
        }
        return aadhaarMaskResponse;
    }

    public ParseDocResponse parseDocAndMask(String key, String type) {
        ParseDocResponse parseDocResponse = null;
        try {
            ParseDocumentReq parseDocumentReq = new ParseDocumentReq(key, type);
            HttpEntity<ParseDocumentReq> entity = new HttpEntity<ParseDocumentReq>(parseDocumentReq, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/parseDoc";
            LOGGER.info("Sending parse document request to npbCore, url: {}, Request: {}", url, parseDocumentReq);
            ResponseEntity<ParseDocResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    ParseDocResponse.class);
            LOGGER.info("Response for parse document received with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                parseDocResponse = responseEntity.getBody();
                LOGGER.info("Response body for parse document  is: {}", parseDocResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while parse document, Exception: {}", e);
        }
        return parseDocResponse;
    }

    public DigilockerLinkResp getDigilockerLink(DigilockerLinkReq digilockerLinkReq) {
        DigilockerLinkResp digilockerLinkResp = null;
        try {
            HttpEntity<DigilockerLinkReq> entity = new HttpEntity<DigilockerLinkReq>(digilockerLinkReq, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/digilockerLink";
            LOGGER.info("Sending sending get digilocker link to npbCore, url: {}, Request: {}", url, digilockerLinkReq);
            ResponseEntity<DigilockerLinkResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    DigilockerLinkResp.class);
            LOGGER.info("Response for get digilocker link received with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                digilockerLinkResp = responseEntity.getBody();
                LOGGER.info("Response body for get digilocker link  is: {}", digilockerLinkResp);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while get digilocker link, Exception: {}", e);
        }
        return digilockerLinkResp;
    }

    public DigilockerDataResp getDigilockerData(DigilockerDataReq digilockerDataReq) {
        DigilockerDataResp digilockerDataResp = null;
        try {
            HttpEntity<DigilockerDataReq> entity = new HttpEntity<DigilockerDataReq>(digilockerDataReq, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/digilockerData";
            LOGGER.info("Sending sending get digilocker data to npbCore, url: {}, Request: {}", url, digilockerDataReq);
            ResponseEntity<DigilockerDataResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    DigilockerDataResp.class);
            LOGGER.info("Response for get digilocker data received with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                digilockerDataResp = responseEntity.getBody();
                LOGGER.info("Response body for get digilocker data  is: {}", digilockerDataResp);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while get digilocker data, Exception: {}", e);
        }
        return digilockerDataResp;
    }

    public CreditBureauResponse createCreditScore(CreditScoreRequest creditScoreRequest) {
        CreditBureauResponse creditBureauResponse = null;
        try {
            HttpEntity<CreditScoreRequest> request = new HttpEntity<CreditScoreRequest>(creditScoreRequest, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/creditBureau";
            LOGGER.info("Sending credit score request for consumer id: {} on url: {} , params : {}", creditScoreRequest.getConsumerId(), url, creditScoreRequest);
            ResponseEntity<CreditBureauResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
                    CreditBureauResponse.class);
            LOGGER.info("Response for credit score received for consumer id: {} with response entity: {}", creditScoreRequest.getConsumerId(),
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                creditBureauResponse = responseEntity.getBody();
                LOGGER.info("Response body for credit score received for consumer id: {} is: {}", creditScoreRequest.getConsumerId(), creditBureauResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for consumer id: " + creditScoreRequest.getConsumerId(),
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for consumer id: ", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while credit score request for consumer id:" + creditScoreRequest.getConsumerId(), e);
        }
        return creditBureauResponse;
    }

    public CreditScoreProviderResponse searchCreditScore(CreditBureauScoreSearchRequest creditBureauScoreSearchRequest) {
        CreditScoreProviderResponse creditScoreProviderResponse = null;
        try {
            HttpEntity<CreditBureauScoreSearchRequest> request = new HttpEntity<CreditBureauScoreSearchRequest>(creditBureauScoreSearchRequest, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/creditBureau/search/creditScore";
            LOGGER.info("Sending search credit score request for url: {} , params : {}", url, creditBureauScoreSearchRequest);
            ResponseEntity<CreditScoreProviderResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
                    CreditScoreProviderResponse.class);
            LOGGER.info("Response for search credit score received  with response entity: {}", responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                creditScoreProviderResponse = responseEntity.getBody();
                LOGGER.info("Response body for search credit score received is: {}", creditScoreProviderResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}  " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}  " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while search credit score " + e);
        }
        return creditScoreProviderResponse;
    }

    public MandateResponse createMandateRequest(MandateDto mandateDto) {
        MandateRequest request = MandateRequest.builder()
                .orderId(mandateDto.getOrderId())
                .firstName(mandateDto.getFirstName())
                .lastName(mandateDto.getLastName())
                .amount(mandateDto.getAmount())
                .mandateProvider(mandateDto.getMandateProvider())
                .email(mandateDto.getEmail())
                .mobile(mandateDto.getMobile())
                .returnUrl(mandateDto.getReturnUrl())
                .paymentDetails(MandatePaymentDetails.builder()
                        .accountHolderName(mandateDto.getAccountHolderName())
                        .accountType(mandateDto.getAccountType())
                        .bankName(mandateDto.getBankName())
                        .cardType(mandateDto.getCardType())
                        .firstCollectionDate(mandateDto.getFirstCollectionDate())
                        .frequency(mandateDto.getFrequency())
                        .ifscCode(mandateDto.getIfscCode())
                        .accountNumber(mandateDto.getAccountNumber())
                        .build())
                .prospectId(mandateDto.getProspectId())
                .loanReferenceId(mandateDto.getLoanReferenceId())
                .applicationNumber(mandateDto.getApplicationNumber())
                .lenderCode(mandateDto.getLenderCode())
                .build();
        String consumerId = mandateDto.getConsumerId();
        MandateResponse mandateResponse = null;
        try {
            HttpEntity<MandateRequest> entity = new HttpEntity<>(request, populateHeaders());
            String url = ntbCoreBaseUrl + "/api/v1/mandates";
            LOGGER.info("Sending create mandate request for consumer id: {} on url: {} with params: {}",
                    consumerId, url, objectMapper.writeValueAsString(request));
            ResponseEntity<MandateResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    MandateResponse.class);
            LOGGER.info("Response for create mandate received for consumer id: {} with response entity: {}", consumerId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                mandateResponse = responseEntity.getBody();
                LOGGER.info("Response for create mandate for consumer id: {} is: {}", consumerId, mandateResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for consumer id: " + consumerId,
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for consumer id: " + consumerId,
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending create mandate request for consumer id:" + consumerId, e);
        }
        return mandateResponse;
    }

    public MandateResponse getMandateDetails(String orderId) {
        MandateResponse mandateResponse = null;
        try {
            String url = ntbCoreBaseUrl + "/api/v1/mandates/" + orderId;
            LOGGER.info("Sending get mandate request for order id: {} on url: {}", orderId, url);
            ResponseEntity<MandateResponse> responseEntity = restTemplate.getForEntity(url, MandateResponse.class);
            LOGGER.info("Response for create mandate received for order id: {} with response entity: {}", orderId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                mandateResponse = responseEntity.getBody();
                LOGGER.info("Response for create mandate for order id: {} is: {}", orderId, mandateResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for consumer id: " + orderId,
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for consumer id: " + orderId,
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending create mandate request for consumer id:" + orderId, e);
        }
        return mandateResponse;
    }

    public BankDetailsResp getBankDetails(String ifsc) {
        try {
            String url = ntbCoreBaseUrl + "/api/v1/bankDetails?ifsc=" + ifsc;
            ResponseEntity<BankDetailsResp> responseEntity = restTemplate.getForEntity(url, BankDetailsResp.class);
            LOGGER.info("Response from bank details {} code {}", responseEntity.getBody(), responseEntity.getStatusCodeValue());
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response {}", e.getResponseBodyAsString());
        }
        return null;
    }

    public KYCResponseDto kycParseAadhaarFile(OKycUploadRequest oKycUploadRequest) {
        try {
            HttpHeaders headers = populateHeaders();
            String url = ntbCoreBaseUrl + "/api/v1/aadhaarXml/upload";
            HttpEntity<OKycUploadRequest> entity = new HttpEntity<>(oKycUploadRequest, headers);
            LOGGER.info("Sending upload okyc details request on url: {} with params: {}",
                    url, objectMapper.writeValueAsString(oKycUploadRequest));
            ResponseEntity<KYCResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, KYCResponseDto.class);
            LOGGER.info("Received okyc details response entity: {}",
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                KYCResponseDto kycResponseDto = responseEntity.getBody();
                LOGGER.info("Received okyc details response with response body: {}",
                        objectMapper.writeValueAsString(kycResponseDto));
                return kycResponseDto;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response {}", e.getResponseBodyAsString());
            if (Util.isNotNull(e) && StringUtils.hasText(e.getResponseBodyAsString()) && e.getResponseBodyAsString().contains("code")) {
                try {
                    return Util.convertToJsonObject(e.getResponseBodyAsString(), KYCResponseDto.class);
                } catch (Exception ex) {
                    LOGGER.error("Exception occurred while parsing the HttpClientErrorException response body: " + e.getResponseBodyAsString(), ex);
                }
            }
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while uploading okyc document request ", e);
        }
        return null;
    }
    public GSTDetails getGstDetails(String gst){
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("",httpHeaders);
            ResponseEntity<GSTDetails> response =
                    restTemplate.exchange(ntbCoreBaseUrl + "/internal/api/v1/details/gst?gst="+gst, HttpMethod.GET, entity, GSTDetails.class);
            LOGGER.info("getGstDetails response received: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new FreewayException("something went wrong" + e);
        }
        return null;
    }




}
