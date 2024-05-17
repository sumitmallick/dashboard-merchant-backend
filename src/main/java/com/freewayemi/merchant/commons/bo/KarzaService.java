package com.freewayemi.merchant.commons.bo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.dto.karza.*;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.FreewayValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;

@Component
public class KarzaService {

    private final String karzaBaseURL;
    private final String gstAuthBaseURL;
    private final String clientKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(KarzaService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KarzaService(@Value("${karza.base.url}") String karzaBaseURL,
                        @Value("${karza.gst.base.url}") String gstAuthBaseURL,
                        @Value("${karza.client.key}") String clientKey) {
        this.karzaBaseURL = karzaBaseURL;
        this.gstAuthBaseURL = gstAuthBaseURL;
        this.clientKey = clientKey;
    }

    /*
            PAN Card Authentication
            API to authenticate Permanent Account Number (PAN) by Income Tax Dept. of India
            karza API - /v2/pan
         */
    public PanAuthResponse PanAuthAPI(String pan, String consent) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            PanAuthRequest req = new PanAuthRequest(consent, pan);
            HttpEntity<PanAuthRequest> entity = new HttpEntity<>(req, headers);
            LOGGER.info("Sending pan auth request to karza with params: {}", req);
            ResponseEntity<PanAuthResponse> response = restTemplate.postForEntity(karzaBaseURL + "v2/pan",
                    entity, PanAuthResponse.class);
            LOGGER.info("Received response for pan auth request from karza with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(response.getBody());

                if (response.getBody().getStatusCode().equals("101")) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "PanAuthAPI");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayValidationException("Pan", "The PAN Card ID does not match the details in the system. Please check and add again");
        } catch (HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayValidationException("Pan", "The PAN Card ID does not match the details in the system. Please check and add again");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /*
        PAN Status Check
        API to authenticate the status and details of given PAN
        karza API - /v2/pan-authentication
     */
    public PanStatusCheckResponse PANStatusCheck(String pan, String name, String dob) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            PanStatusCheckRequest req = new PanStatusCheckRequest(pan, name, dob, "Y");
            HttpEntity<PanStatusCheckRequest> entity = new HttpEntity<>(req, headers);
            ResponseEntity<PanStatusCheckResponse> response = restTemplate.exchange(karzaBaseURL + "v2/pan-authentication", HttpMethod.POST,
                    entity, PanStatusCheckResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(response.getBody());
                if (response.getBody().getStatusCode().equals("101")) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "PANStatusCheck");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayException("The details provided by you are incorrect. Please check and add again");
        } catch (HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new FreewayException("The details provided by you are incorrect. Please check and add again");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /*
        Name Similarity
        API to match given names with Names as per KYC documents for individuals and entities
        /v3/name
     */
    public NameCheckResponse NameSimilarityAPI(String name1, String name2) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            NameCheckRequest req = new NameCheckRequest(name1, name2, "individual", "L");
            LOGGER.info("Sending name similarity api request to karza with params: {}", req);
            HttpEntity<NameCheckRequest> entity = new HttpEntity<>(req, headers);
            ResponseEntity<NameCheckResponse> response = restTemplate.exchange(karzaBaseURL + "/v3/name", HttpMethod.POST,
                    entity, NameCheckResponse.class);
            LOGGER.info("Response received for name similarity api from karza: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Response body: {} for name similarity api from karza", response);
                if (null != response.getBody() && "101".equalsIgnoreCase(response.getBody().getStatusCode())) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "NameSimilarityAPI");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            LOGGER.error("Client error exception occurred while sending name similarity request to karza: " + e.getResponseBodyAsString());
            throw new FreewayException("The name provided does not match the details on your PAN Card. Please check and add again");
        } catch (HttpServerErrorException e) {
            LOGGER.error("Server error exception occurred while sending name similarity request to karza: " + e.getResponseBodyAsString());
            throw new FreewayException("The name provided does not match the details on your PAN Card. Please check and add again");
        } catch (Exception e) {
            LOGGER.error("Exception error exception occurred while sending name similarity request to karza: " + e);
        }
        return null;
    }

    /*
        Bank Account Verification
        API to verify bank account holder name basis IFSC and account number
        /v2/bankacc
     */
    public BankAccVerificationResponse BankAccountVerification(String ifsc, String accountNumber) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            BankAccVerificationRequest req = new BankAccVerificationRequest("Y", ifsc, accountNumber);
            String url = karzaBaseURL + "/v2/bankacc";
            HttpEntity<BankAccVerificationRequest> entity = new HttpEntity<>(req, headers);
            LOGGER.info("Sending bank account verification request to karza on url: {} with params: {}", url, req);
            ResponseEntity<BankAccVerificationResponse> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, BankAccVerificationResponse.class);
            LOGGER.info("Received response for bank account verification request from karza with response: {}", response);
            if (response.getStatusCode().is2xxSuccessful() && null != response.getBody()) {
                LOGGER.info("Received response body of bank account verification : {}", response.getBody());
                if ("101".equals(response.getBody().getStatusCode())) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "BankAccountVerification");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {}", e.getResponseBodyAsString());
            throw new FreewayException("Transaction failed for given account number");
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {}", e.getResponseBodyAsString());
            throw new FreewayException("Transaction failed for given account number");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while verifying bank account details", e);
        }
        return null;
    }

    public FaceMatchingResponse FaceMatching(String image1B64, String image2B64) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        FaceMatchingRequest req = new FaceMatchingRequest(image1B64, image2B64);
        HttpEntity<FaceMatchingRequest> entity = new HttpEntity<>(req, headers);
        try {
            String url = karzaBaseURL + "v3/facesimilarity";
            LOGGER.info("Sending face similarity request on url: {} to karza service with request params: {}", url, req);
            ResponseEntity<FaceMatchingResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    FaceMatchingResponse.class);
            LOGGER.info("Response received : {} from karza for face similarity request", response);
            if (response.getStatusCode().is2xxSuccessful() && null != response.getBody()) {
                LOGGER.info("Response body for face similarity api: {}", response.getBody());
                if ("101".equals(response.getBody().getStatusCode())) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "FaceMatching");
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending face matching request to karza service: ", e);
        }
        LOGGER.error("Face matching failed");
        return null;
    }

    public GstAuthenticationResponse GSTAuth(String gstin) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            GstAuthenticationRequest gstAuthenticationRequest = new GstAuthenticationRequest("Y", true, gstin);
            HttpEntity<GstAuthenticationRequest> entity = new HttpEntity<>(gstAuthenticationRequest, headers);
            LOGGER.info("Sending gst verification request to karza for gst: {}", gstin);
            ResponseEntity<GstAuthenticationResponse> response = restTemplate.exchange(gstAuthBaseURL + "v1/gstdetailed", HttpMethod.POST,
                    entity, GstAuthenticationResponse.class);
            LOGGER.info("Received gst verification response from karza: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(response.getBody());
                return response.getBody();
            }
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            LOGGER.error(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error(e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public TotalOCRResponse OCR(String file64, String docType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = GetHeaders();
        try {
            TotalOCRRequest req = new TotalOCRRequest(file64, true, true, false, docType);
            HttpEntity<TotalOCRRequest> entity = new HttpEntity<>(req, headers);
            String url = karzaBaseURL + "v3/ocr/kyc";
            LOGGER.info("Sending aadhaar masking request to karza on url: {} with params: {}",
                    url, req.toString());
            ResponseEntity<TotalOCRResponse> response = restTemplate.exchange(url, HttpMethod.POST,
                    entity, TotalOCRResponse.class);
            LOGGER.info("Received aadhaar mask response from karza response : {} ", response);
            if (response.getStatusCode().is2xxSuccessful() && null != response.getBody()) {
                LOGGER.info("Aadhaar masking response body: {} ", response.getBody());
                if ("101".equals(response.getBody().getStatusCode())) {
                    return response.getBody();
                }
                handleInternalStatusCode(response.getBody().getStatusCode(), "TotalOCRResponse");
            }
            LOGGER.info("Aadhaar masking error response code: {} ", response.getStatusCode());
            handleStatusCodes(response.getStatusCode().value());
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} ",
                    e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} ",
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending aadhaar mask request to karza with response: {} ", e.getMessage());
        }
        return null;
    }

    private HttpHeaders GetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("x-karza-key", clientKey);
        return headers;
    }

    private void handleInternalStatusCode(String statusCode, String api) {
        switch (statusCode) {
            case "102":
                switch (api) {
                    case "PanAuthAPI":
                        throw new FreewayException("The PAN Card ID does not match the details in the system. Please check and add again");
                    case "PANStatusCheck":
                        throw new FreewayException("Date of birth does not match the details on your PAN Card. Please check and add again");
                    case "BankAccountVerification":
                        throw new FreewayException("IFSC code or account number is not valid. Please check and add again");
                    default:
                        throw new FreewayException("Invalid ID number or combination of inputs");
                }
            case "103":
                throw new FreewayException("No records found for the given ID or combination of inputs");
            case "104":
                throw new FreewayException("Max retries exceeded");
            case "105":
                throw new FreewayException("Missing Consent");
            case "106":
                throw new FreewayException("Multiple Records Exist");
            case "107":
                throw new FreewayException("Not Supported");
            case "108":
                throw new FreewayException("Internal Resource Unavailable");
            case "109":
                throw new FreewayException("Too many records Found");
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

    public AadhaarConsentResponse sendAadhaarConsent(AadhaarXmlRequestDto aadhaarXmlRequestDto) throws IOException {
        String userId = aadhaarXmlRequestDto.getUserId();
        try {
            AadhaarConsentRequest aadhaarConsentRequest = AadhaarConsentRequest.builder()
                    .consent("Y")
                    .consentText(aadhaarXmlRequestDto.getConsentText())
                    .consentTime(String.valueOf(Instant.now().getEpochSecond()))
                    .clientData(ClientData.builder().caseId(aadhaarXmlRequestDto.getUserId()).build())
                    .ipAddress(aadhaarXmlRequestDto.getUserIpAddress())
                    .userAgent(aadhaarXmlRequestDto.getUserAgent())
                    .name(aadhaarXmlRequestDto.getUserName())
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/aadhaar-consent";
            HttpEntity<AadhaarConsentRequest> entity = new HttpEntity<>(aadhaarConsentRequest, headers);
            LOGGER.info("Sending aadhaar consent request for user id: {} on url: {} with params: {}", userId,
                    url, objectMapper.writeValueAsString(aadhaarConsentRequest));
            ResponseEntity<AadhaarConsentResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, AadhaarConsentResponse.class);
            LOGGER.info("Received aadhaar consent response for user id: {} with response entity: {}", userId,
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                AadhaarConsentResponse aadhaarConsentResponse = responseEntity.getBody();
                LOGGER.info("Received aadhaar consent response body for user id: {} with response body: {}", userId,
                        objectMapper.writeValueAsString(aadhaarConsentResponse));
                return aadhaarConsentResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for user id: {}", e.getResponseBodyAsString(), userId);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for application id: {}", e.getResponseBodyAsString(), userId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending aadhaar consent request of user id: {}, Exception: {}", userId, e);
        }
        return null;
    }

    public AadhaarOtpResponse sendAadhaarOtpRequest(AadhaarXmlRequestDto aadhaarXmlRequestDto) {
        String userId = aadhaarXmlRequestDto.getUserId();
        try {
            AadhaarOtpRequest aadhaarOtpRequest = AadhaarOtpRequest.builder()
                    .consent("Y")
                    .accessKey(aadhaarXmlRequestDto.getAccessKey())
                    .aadhaarNo(aadhaarXmlRequestDto.getAadhaarNumber())
                    .clientData(ClientData.builder().caseId(aadhaarXmlRequestDto.getUserId()).build())
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/get-aadhaar-otp";
            HttpEntity<AadhaarOtpRequest> entity = new HttpEntity<>(aadhaarOtpRequest, headers);
            LOGGER.info("Sending aadhaar otp request to linked contact number for user id: {} on url: {} with params: {}", userId,
                    url, objectMapper.writeValueAsString(aadhaarOtpRequest));
            ResponseEntity<AadhaarOtpResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, AadhaarOtpResponse.class);
            LOGGER.info("Received response for aadhaar otp request for user id: {} with response entity: {}", userId,
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                AadhaarOtpResponse aadhaarOtpResponse = responseEntity.getBody();
                LOGGER.info("Received response body for aadhaar otp request user id: {} with response body: {}", userId,
                        objectMapper.writeValueAsString(aadhaarOtpResponse));
                return aadhaarOtpResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for user id: {}", e.getResponseBodyAsString(), userId);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for user id: {}", e.getResponseBodyAsString(), userId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending otp to contact number linked with customer aadhaar for user id:{}, Exception: {} ", userId, e);
        }
        return null;
    }

    public AadhaarFileResponse getAadhaarVerifyOrFile(AadhaarXmlRequestDto aadhaarXmlRequestDto) throws JsonProcessingException {
        String userId = aadhaarXmlRequestDto.getUserId();
        try {
            AadhaarFileRequest aadhaarFileRequest = AadhaarFileRequest.builder()
                    .consent("Y")
                    .accessKey(aadhaarXmlRequestDto.getAccessKey())
                    .otp(aadhaarXmlRequestDto.getOtp())
                    .shareCode(aadhaarXmlRequestDto.getShareCode())
                    .clientData(ClientData.builder().caseId(aadhaarXmlRequestDto.getUserId()).build())
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/get-aadhaar-file";
            HttpEntity<AadhaarFileRequest> entity = new HttpEntity<>(aadhaarFileRequest, headers);
            LOGGER.info("Sending get aadhaar xml file request for user id: {} on url: {} with params: {}", userId,
                    url, objectMapper.writeValueAsString(aadhaarFileRequest));
            ResponseEntity<AadhaarFileResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, AadhaarFileResponse.class);
            LOGGER.info("Received response for get aadhaar xml file request for user id: {} with response entity: {}", userId,
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                AadhaarFileResponse aadhaarFileResponse = responseEntity.getBody();
                LOGGER.info("Received response body for get aadhaar xml file request user id: {} with response body: {}", userId,
                        objectMapper.writeValueAsString(aadhaarFileResponse));
                return aadhaarFileResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} for user id: {}", e.getResponseBodyAsString(), userId);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} for user id: {}", e.getResponseBodyAsString(), userId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending get aadhaar file request for user id:{}, Exception: {} ", userId, e);
        }
        return null;
    }


    public DigilockerLinkResponse getDigilockerLink(DigilockerLinkRequest request) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/digilocker/link";
            HttpEntity<DigilockerLinkRequest> entity = new HttpEntity<>(request, headers);
            LOGGER.info("Sending digilocker get link  on url: {} with params: {}",
                    url, objectMapper.writeValueAsString(request));
            ResponseEntity<DigilockerLinkResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, DigilockerLinkResponse.class);
            LOGGER.info("Received digilocker get link response entity: {}",
                    objectMapper.writeValueAsString(responseEntity.getBody()));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                DigilockerLinkResponse digilockerLinkResponse = responseEntity.getBody();
                LOGGER.info("Received digilocker get link response with response body: {}",
                        objectMapper.writeValueAsString(digilockerLinkResponse));
                return digilockerLinkResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending digilocker get link request ", e);
        }
        return null;
    }

    public DigilockerDocumentsResponse getDigilockerDocList(DigilockerDocumentsRequest request) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/digilocker/documents";
            HttpEntity<DigilockerDocumentsRequest> entity = new HttpEntity<>(request, headers);
            LOGGER.info("Sending digilocker get link  on url: {} with params: {}",
                    url, objectMapper.writeValueAsString(request));
            ResponseEntity<DigilockerDocumentsResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, DigilockerDocumentsResponse.class);
            LOGGER.info("Received digilocker get link response entity: {}",
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                DigilockerDocumentsResponse digilockerDocResponse = responseEntity.getBody();
                LOGGER.info("Received digilocker get doc list response with response body: {}",
                        objectMapper.writeValueAsString(digilockerDocResponse));
                return digilockerDocResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending digilocker get doc list request ", e);
        }
        return null;
    }

    public DigilockerDownloadResponse getDigilockerDocDownload(DigilockerDownloadRequest request) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = GetHeaders();
            String url = karzaBaseURL + "v3/digilocker/download";
            HttpEntity<DigilockerDownloadRequest> entity = new HttpEntity<>(request, headers);
            LOGGER.info("Sending digilocker document download  on url: {} with params: {}",
                    url, objectMapper.writeValueAsString(request));
            ResponseEntity<DigilockerDownloadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
                    entity, DigilockerDownloadResponse.class);
            LOGGER.info("Received digilocker document download response entity: {}",
                    objectMapper.writeValueAsString(responseEntity));
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                DigilockerDownloadResponse digilockerDownloadResponse = responseEntity.getBody();
                LOGGER.info("Received digilocker document download response with response body: {}",
                        objectMapper.writeValueAsString(digilockerDownloadResponse));
                return digilockerDownloadResponse;
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending digilocker document download request ", e);
        }
        return null;
    }
}


