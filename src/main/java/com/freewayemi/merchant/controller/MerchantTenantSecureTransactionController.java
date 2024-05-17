package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.*;

import com.freewayemi.merchant.bo.MerchantSecureTransactionBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;

import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.SecureTransactionRequest;
import com.freewayemi.merchant.commons.dto.SecureTransactionResponse;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.EligibilityApiType;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.MerchantOfferRequest;
import com.freewayemi.merchant.dto.MerchantPriceResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class MerchantTenantSecureTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantTenantSecureTransactionController.class);

    private final MerchantSecureTransactionBO merchantSecureTransactionBO;
    private final MerchantSecureEligibilitiesBO merchantSecureEligibilitiesBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantTransactionBO merchantTransactionBO;
    private final AuthCommonService authCommonService;

    private final PaymentServiceBO paymentServiceBO;

    @Autowired
    public MerchantTenantSecureTransactionController(MerchantSecureTransactionBO merchantSecureTransactionBO,
                                                     MerchantUserBO merchantUserBO, AuthCommonService authCommonService,
                                                     MerchantSecureEligibilitiesBO merchantSecureEligibilitiesBO,
                                                     MerchantTransactionBO merchantTransactionBO, PaymentServiceBO paymentServiceBO) {
        this.merchantSecureTransactionBO = merchantSecureTransactionBO;
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
        this.merchantSecureEligibilitiesBO = merchantSecureEligibilitiesBO;
        this.merchantTransactionBO = merchantTransactionBO;
        this.paymentServiceBO = paymentServiceBO;
    }

    @PostMapping("/{tenant}/secure/api/v1/transactions")
    public SecureTransactionResponse initiateTransaction(@RequestBody SecureTransactionRequest request,
                                                         @PathVariable("tenant") String tenant,
                                                         HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure seamless transaction request received: {}", request);
        if (!StringUtils.hasText(httpServletRequest.getHeader("X-API-KEY"))){
            throw new FreewayException("X-API-KEY is required");
        }
        String headerApiKey = httpServletRequest.getHeader("X-API-KEY");
        MerchantUser mu = merchantUserBO.getMerchantUserByKey(headerApiKey);
        return  merchantSecureTransactionBO.createPgTransaction(mu, request, tenant);
    }

    @GetMapping("/{tenant}/secure/api/v1/transactions/{tid}")
    public ResponseEntity<?> transactionInquiry(@PathVariable("tid") String orderIdOrTransactionId,
                                                @PathVariable("tenant") String tenant,
                                                HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure inquiry transaction request received: {}", orderIdOrTransactionId);
        if (!StringUtils.hasText(httpServletRequest.getHeader("X-API-KEY"))){
            throw new FreewayException("X-API-KEY is required");
        }
        String headerApiKey = httpServletRequest.getHeader("X-API-KEY");
        MerchantUser mu = merchantUserBO.getMerchantUserByKey(headerApiKey);
        LOGGER.info("Secure transaction enquiry request for transaction id: {} and merchant display id: {}",
                orderIdOrTransactionId, mu.getDisplayId());
        return merchantSecureTransactionBO.inquiry(mu, orderIdOrTransactionId, tenant);
    }

    @PostMapping("/{tenant}/secure/api/v1/transactions/{paymentTxnId}/refunds")
    public SecureTransactionResponse createRefundTransaction(@PathVariable("paymentTxnId") String paymentTxnId,
                                                             @PathVariable("tenant") String tenant,
                                                             @RequestBody SecureTransactionRequest request,
                                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure refund request received for transaction id: {} with params: {}", paymentTxnId, request);
        if (!StringUtils.hasText(httpServletRequest.getHeader("X-API-KEY"))){
            throw new FreewayException("X-API-KEY is required");
        }
        String headerApiKey = httpServletRequest.getHeader("X-API-KEY");
        MerchantUser mu = merchantUserBO.getMerchantUserByKey(headerApiKey);
        LOGGER.info("Secure transaction enquiry request for transaction id: {} and merchant display id: {}",
                paymentTxnId, mu.getDisplayId());

        if(StringUtils.hasText(paymentTxnId) && StringUtils.hasText(tenant)) {
            TransactionResponse tr = paymentServiceBO.getTransactionById(paymentTxnId);
            if(Util.isNotNull(tr) && StringUtils.hasText(tr.getPartner())){
                if(!tenant.equals(tr.getPartner())) {
                    throw new FreewayException("Transaction created with Invalid partner" + tr.getPartner());
                }
            }
        }
        return merchantSecureTransactionBO.processRefundTransaction(mu, paymentTxnId, request, tenant);
    }

    @PostMapping("/{tenant}/secure/api/v1/checkEligibility")
    public SecureTransactionResponse checkEligibility(@RequestBody SecureTransactionRequest request,
                                                      @PathVariable("tenant") String tenant,
                                                      HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure request received to check eligibility with params: {}", request);
        try {
            if (!StringUtils.hasText(httpServletRequest.getHeader("X-API-KEY"))){
                throw new FreewayException("X-API-KEY is required");
            }
            String headerApiKey = httpServletRequest.getHeader("X-API-KEY");
            MerchantUser mu = merchantUserBO.getMerchantUserByKey(headerApiKey);
            LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
            SecureTransactionResponse secureTransactionResponse =  merchantSecureEligibilitiesBO.getEligibilities(mu, request,
                    EligibilityApiType.MERCHANT_SECURE_ELIGIBILITY_API.name(), false, tenant);
            LOGGER.info("secureTransactionResponse: {}", secureTransactionResponse);
            return secureTransactionResponse;
        } catch (FreewayException e) {
            LOGGER.error("FreewayException while checking eligibility: ", e);
            throw e;
        }
    }

    @PostMapping("/{tenant}/api/v1/eligible-offers")
    public Map<String, Map<String, List<MerchantPriceResponse>>> getEligibleOffers(
            @Validated @RequestBody MerchantOfferRequest request,
            HttpServletRequest httpServletRequest) {

        LOGGER.info("Received get merchant eligible offers request with parameters: {}", request);
        if (!StringUtils.hasText(httpServletRequest.getHeader("X-API-KEY"))){
            throw new FreewayException("X-API-KEY is required");
        }
        String headerApiKey = httpServletRequest.getHeader("X-API-KEY");
        MerchantUser mu = merchantUserBO.getMerchantUserByKey(headerApiKey);
        return merchantTransactionBO.getOffers(mu.getDisplayId(), request, true, false);
    }
}
