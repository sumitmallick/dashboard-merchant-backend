package com.freewayemi.merchant.commons.ntbservice.bo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.dto.NTBLoanInfoDTO;
import com.freewayemi.merchant.commons.dto.ntbservices.CancelLoanRequestDto;
import com.freewayemi.merchant.commons.dto.ntbservices.CancelLoanResponseDto;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.ntbservice.dto.*;
import com.freewayemi.merchant.commons.ntbservice.helper.NtbServiceConstants;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.type.TransactionStatus;
import com.freewayemi.merchant.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@EnableAsync
public class NtbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NtbService.class);

    private final String ntbServiceBaseUrl = "http://ntbservicesms/ntbservices";
    private final RestTemplate restTemplate;
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static String SOURCE = "Internal";

    @Autowired
    public NtbService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NtbLoanEligibilityResponse checkLoanEligibility(NtbLoanDto ntbLoanDto, String urlEndPoint) throws IOException {
        NtbLoanEligibilityResponse ntbLoanEligibilityResponse = null;
        String transactionId = ntbLoanDto.getTransactionId();
        String consumerMobile = ntbLoanDto.getMobileNumber();
        try {
            NtbLoanEligibilityRequest ntbLoanEligibilityRequest = NtbLoanEligibilityRequest.builder()
                    .mobileNumber(ntbLoanDto.getMobileNumber())
                    .provider(ntbLoanDto.getProvider())
                    .amount(ntbLoanDto.getAmount())
                    .pan(ntbLoanDto.getPan())
                    .latitude(ntbLoanDto.getLatitude())
                    .longitude(ntbLoanDto.getLongitude())
                    .ip(ntbLoanDto.getIp())
                    .build();
            HttpEntity<NtbLoanEligibilityRequest> entity = new HttpEntity<>(ntbLoanEligibilityRequest, populateHeaders());
            String url = ntbServiceBaseUrl + urlEndPoint;
            LOGGER.info("Sending loan eligibility request for consumer mobile: {} transaction id: {} on url: {} with params: {}",
                    consumerMobile, transactionId, url, ntbLoanEligibilityRequest);
            ResponseEntity<NtbLoanEligibilityResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    NtbLoanEligibilityResponse.class);
            LOGGER.info("Response for loan eligibility received for consumer mobile: {} transaction id: {} with response entity: {}",
                    consumerMobile, transactionId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ntbLoanEligibilityResponse = responseEntity.getBody();
                LOGGER.info("Response body for loan eligibility response: {}", ntbLoanEligibilityResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} while sending loan eligibility request"
                    + " to NTB for consumer mobile: {} paymentTxnId: " + transactionId, consumerMobile, e.getResponseBodyAsString());
            if (StringUtils.hasText(e.getResponseBodyAsString()) && e.getResponseBodyAsString().contains("code")) {
                ntbLoanEligibilityResponse =
                        objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanEligibilityResponse.class);
            } else throw e;
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} while sending loan eligibility request"
                    + " to NTB for consumer mobile: {} paymentTxnId: " + transactionId, consumerMobile, e.getResponseBodyAsString());
            if (StringUtils.hasText(e.getResponseBodyAsString()) && e.getResponseBodyAsString().contains("code")) {
                ntbLoanEligibilityResponse =
                        objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanEligibilityResponse.class);
            } else throw e;
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending loan eligibility request to NTB for  consumer mobile: {} paymentTxnId: "
                    + transactionId, consumerMobile, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending loan eligibility request to NTB for consumer mobile: {} paymentTxnId: "
                    + transactionId, consumerMobile, e);
            throw e;
        }
        return ntbLoanEligibilityResponse;
    }

    public NtbLoanUpdateEmiDetailsResponse updateLoanEmiDetails(NtbLoanDto ntbLoanDto) throws IOException {
        String transactionId = ntbLoanDto.getTransactionId();
        NtbLoanUpdateEmiDetailsResponse ntbLoanUpdateEmiDetailsResponse = null;
        try {
            NtbLoanUpdateEmiDetailsRequest ntbLoanUpdateEmiDetailsRequest = NtbLoanUpdateEmiDetailsRequest.builder()
                    .prospectId(String.valueOf(ntbLoanDto.getProspectId()))
                    .updateType(NtbServiceConstants.EMI_DETAILS)
                    .amount(String.valueOf(ntbLoanDto.getAmount()))
                    .paymentDiscount(String.valueOf(ntbLoanDto.getDiscount()))
                    .emiAmount(String.valueOf(ntbLoanDto.getEmi()))
                    .downpaymentAmount(String.valueOf(ntbLoanDto.getDownpaymentAmount()))
                    .roi(String.valueOf(ntbLoanDto.getRoi()))
                    .principal(String.valueOf(ntbLoanDto.getPgAmount()))
                    .tenure(String.valueOf(ntbLoanDto.getTenure()))
                    .transactionId(transactionId)
                    .build();
            HttpEntity<NtbLoanUpdateEmiDetailsRequest> entity = new HttpEntity<>(ntbLoanUpdateEmiDetailsRequest, populateHeaders());
            String url = ntbServiceBaseUrl + NtbServiceConstants.LOAN_UPDATE_EMI_DETAILS_ENDPOINT + ntbLoanDto.getProspectId();
            LOGGER.info("Sending update loan emi details for transaction id: {} on url: {} with params: {}",
                    transactionId, url, ntbLoanUpdateEmiDetailsRequest);
            ResponseEntity<NtbLoanUpdateEmiDetailsResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    NtbLoanUpdateEmiDetailsResponse.class);
            LOGGER.info("Response for update loan emi details received for transaction id: {} with response entity: {}",
                    transactionId, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ntbLoanUpdateEmiDetailsResponse = new NtbLoanUpdateEmiDetailsResponse("0", "success");
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} while sending update loan emi details request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanUpdateEmiDetailsResponse =
                    objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanUpdateEmiDetailsResponse.class);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} while sending update loan emi details request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanUpdateEmiDetailsResponse =
                    objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanUpdateEmiDetailsResponse.class);
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending update loan emi details request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending update loan emi details request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        }
        return ntbLoanUpdateEmiDetailsResponse;
    }

    public NtbLoanDisbursementResponse disburseLoan(NtbLoanDto ntbLoanDto) throws IOException {
        NtbLoanDisbursementResponse ntbLoanDisbursementResponse = null;
        String transactionId = ntbLoanDto.getTransactionId();
        try {
            NtbLoanDisbursementRequest ntbLoanDisbursementRequest = NtbLoanDisbursementRequest.builder()
                    .amount(String.valueOf(ntbLoanDto.getPgAmount()))
                    .prospectId(ntbLoanDto.getProspectId())
                    .tenure(String.valueOf(ntbLoanDto.getTenure()))
                    .processingFee(String.valueOf(ntbLoanDto.getProcessingFee()))
                    .gstOnProcessingFee(String.valueOf(ntbLoanDto.getGstOnProcessingFee()))
                    .roi(String.valueOf(ntbLoanDto.getRoi()))
                    .transactionId(transactionId)
                    .build();
            HttpEntity<NtbLoanDisbursementRequest> entity = new HttpEntity<>(ntbLoanDisbursementRequest, populateHeaders());
            String url = ntbServiceBaseUrl + NtbServiceConstants.LOAN_DISBURSE_ENDPOINT;
            LOGGER.info("Sending loan disburse request for transaction id: {} on url: {} with params: {}", transactionId, url, ntbLoanDisbursementRequest);
            ResponseEntity<NtbLoanDisbursementResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity,
                    NtbLoanDisbursementResponse.class);
            LOGGER.info("Response for loan disburse received for transaction id: {} with response entity: {}", transactionId,
                    responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ntbLoanDisbursementResponse = responseEntity.getBody();
                LOGGER.info("Response body for loan disburse response: {}", ntbLoanDisbursementResponse);
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} while sending loan disburse request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanDisbursementResponse =
                    objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanDisbursementResponse.class);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} while sending loan disburse request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanDisbursementResponse =
                    objectMapper.readValue(e.getResponseBodyAsString(), NtbLoanDisbursementResponse.class);
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending loan disburse request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending loan disburse request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        }
        return ntbLoanDisbursementResponse;
    }

    public String getAgreementContent(String transactionId, String prospectId) {
        try {
            String url = ntbServiceBaseUrl + getLoanAgreementEndPoint(prospectId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            HttpEntity<NtbLoanUpdateEmiDetailsRequest> entity = new HttpEntity<>(headers);
            LOGGER.info("Sending get loan agreement content for transaction id: {} on url: {}",
                    transactionId, url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            LOGGER.info("Response for loan agreement content received for transaction id: {} with response entity: {}",
                    transactionId, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} while sending get loan agreement content request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            throw e;
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} while sending get loan agreement content request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            throw e;
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending get loan agreement content request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending get loan agreement content request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        }
        return TransactionStatus.failed.name();
    }

    public NTBLoanInfoDTO getLoanInfoStatus(String transactionId, String prospectId) throws IOException {
        NTBLoanInfoDTO ntbLoanInfoDto = null;
        try {
            String url = ntbServiceBaseUrl + String.format(NtbServiceConstants.LOAN_INFO_ENDPOINT, prospectId);
            HttpEntity<NtbLoanUpdateEmiDetailsRequest> entity = new HttpEntity<>(populateHeaders());
            LOGGER.info("Sending get loan info for transaction id: {} on url: {}",
                    transactionId, url);
            ResponseEntity<NTBLoanInfoDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, NTBLoanInfoDTO.class);
            LOGGER.info("Response for get loan info for transaction id: {} with response entity: {}",
                    transactionId, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ntbLoanInfoDto = responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: {} while sending get loan info request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanInfoDto = objectMapper.readValue(e.getResponseBodyAsString(), NTBLoanInfoDTO.class);
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: {} while sending get loan info request"
                    + " to NTB for paymentTxnId: " + transactionId, e.getResponseBodyAsString());
            ntbLoanInfoDto = objectMapper.readValue(e.getResponseBodyAsString(), NTBLoanInfoDTO.class);
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending get loan info request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending get loan info request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        }
        return ntbLoanInfoDto;
    }

    public String getLoanAgreementEndPoint(String prospectId) {
        return String.format(NtbServiceConstants.LOAN_AGREEMENT_GET_URL, prospectId);
    }

    private static HttpHeaders populateHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-SOURCE", SOURCE);
        return headers;
    }

    public CancelLoanResponseDto refundNtbLoan(NtbLoanDto ntbLoanDto) throws IOException {
        CancelLoanResponseDto cancelLoanResponseDto = null;
        String transactionId = ntbLoanDto.getTransactionId();
        String loanId = ntbLoanDto.getProspectId();
        try {
            CancelLoanRequestDto cancelLoanRequestDto = CancelLoanRequestDto.builder()
                    .refundId(ntbLoanDto.getRefundId())
                    .loanId(ntbLoanDto.getProspectId())
                    .cancellationReason(ntbLoanDto.getCancellationReason())
                    .refundAmount(ntbLoanDto.getRefundAmount())
                    .disbursalReferenceNumber(ntbLoanDto.getDisbursalReferenceNumber())
                    .transactionId(ntbLoanDto.getTransactionId())
                    .build();
            String url = ntbServiceBaseUrl + NtbServiceConstants.LOAN_CANCEL_ENDPOINT;
            HttpEntity<CancelLoanRequestDto> entity = new HttpEntity<>(cancelLoanRequestDto, populateHeaders());
            LOGGER.info("Sending cancel loan request for transaction id: {} and loan id: {} " +
                    "with params: {} on url: {}", loanId, transactionId, cancelLoanRequestDto, url);
            ResponseEntity<CancelLoanResponseDto> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, CancelLoanResponseDto.class);
            LOGGER.info("Response cancel loan received with response entity : {} for transaction " +
                    "id: {} and loan id: {}", responseEntity, transactionId, loanId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                cancelLoanResponseDto = responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred with response: while sending get loan info request"
                    + " to NTB for paymentTxnId: " + transactionId, e);
            if (StringUtils.hasText(e.getResponseBodyAsString()) && e.getResponseBodyAsString().contains("code")) {
                cancelLoanResponseDto = objectMapper.readValue(e.getResponseBodyAsString(), CancelLoanResponseDto.class);
            }
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred with response: " + e.getResponseBodyAsString() + " while sending get loan info request"
                    + " to NTB for paymentTxnId: " + transactionId);
            if (StringUtils.hasText(e.getResponseBodyAsString()) && e.getResponseBodyAsString().contains("code")) {
                cancelLoanResponseDto = objectMapper.readValue(e.getResponseBodyAsString(), CancelLoanResponseDto.class);
            }
        } catch (ResourceAccessException e) {
            LOGGER.error("ResourceAccessException occurred while sending get loan info request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while sending get loan info request to NTB for paymentTxnId: "
                    + transactionId, e);
            throw e;
        }
        return cancelLoanResponseDto;
    }

    @Async
    public void saveProvideConsent(ConsentRequestV2 consentRequestV2) {
        try {
            String url = ntbServiceBaseUrl + "/internal/api/v2/consents";
            HttpEntity<ConsentRequestV2> entity = new HttpEntity<>(consentRequestV2, populateHeaders());
            LOGGER.info("api call to save provider consent: {} - {}", url, entity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("provider consent saved successfully");
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while saving provider consent " + e);
        }
    }

    @Async
    public void checkEligibility(EligibilityCheckRequest eligibilityCheckRequest, String consumerId) {
        try {
            String url = ntbServiceBaseUrl + "/internal/api/v1/lenders/check-eligibility/" + eligibilityCheckRequest.getTransactionId() + "/initiate";
            HttpHeaders headers = populateHeaders();
            headers.add("X-SOURCE-ID", consumerId);
            HttpEntity<EligibilityCheckRequest> entity = new HttpEntity<>(eligibilityCheckRequest, headers);
            LOGGER.info("api call to check eligibility: {} - {}", url, entity);
            ResponseEntity<ProviderEligibilityResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, ProviderEligibilityResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("lender check eligibility successfully");
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred {}", e.getMessage());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while checking lender eligibility " + e);
        }
    }

    public ProviderEligibilityResponse poling(String transactionId, String consumerId, String merchantId) {
        try {
            String url = ntbServiceBaseUrl + "/internal/api/v1/lenders/check-eligibility/" + transactionId + "/polling?consumerId=" + consumerId;
            HttpHeaders headers = populateHeaders();
            headers.add("X-SOURCE-ID", merchantId);
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            LOGGER.info("api call to get eligibilities from poling: {} - {}", url, entity);
            ResponseEntity<ProviderEligibilityResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, ProviderEligibilityResponse.class);
            LOGGER.info("api call to get eligibilities from poling: {} - {}", url, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred {}", e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(e.getResponseBodyAsString()));
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred {}", e.getResponseBodyAsString());
            throw new MerchantException(MerchantResponseCode.getByMessage(e.getResponseBodyAsString()));
        } catch (MerchantException e) {
            LOGGER.error("Exception occurred while checking eligibilities from poling " + e);
            throw e;
        }
        throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

    public List<NtbLoanResponse> getLoanStage(String transactionId, String merchantId) {
        try {
            String url = ntbServiceBaseUrl + "/internal/api/v1/loans/partner?transactionId=" + transactionId;
            HttpHeaders headers = populateHeaders();
            headers.add("X-SOURCE-ID", merchantId);
            headers.add("X-SOURCE", "MERCHANTMS");
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            LOGGER.info("api call to get eligibilities from poling: {} - {}", url, entity);
            ResponseEntity<List<NtbLoanResponse>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<NtbLoanResponse>>() {
            });
            LOGGER.info("api call to get eligibilities from poling: {} - {}", url, responseEntity);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HttpClientErrorException occurred {}", e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            LOGGER.error("HttpServerErrorException occurred {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while checking eligibilities from poling " + e);
        }
        return null;
    }
}
