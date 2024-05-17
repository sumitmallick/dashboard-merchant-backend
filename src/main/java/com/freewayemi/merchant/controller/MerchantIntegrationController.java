package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantEligibilitiesBO;
import com.freewayemi.merchant.bo.MerchantTransactionBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.dto.CheckEligibilityRequest;
import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.dto.PgTransactionResponse;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.EligibilityApiType;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.MerchantEligibleOfferResponse;
import com.freewayemi.merchant.dto.MerchantOfferRequest;
import com.freewayemi.merchant.dto.MerchantPriceResponse;
import com.freewayemi.merchant.dto.response.CheckEligibilityResponse;
import com.freewayemi.merchant.dto.response.ConsumerProfileConstants;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.MerchantAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class MerchantIntegrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantIntegrationController.class);

    private final AuthCommonService authCommonService;
    private final MerchantAuthService merchantAuthService;
    private final MerchantTransactionBO merchantTransactionBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantEligibilitiesBO merchantEligibilitiesBO;

    @Autowired
    public MerchantIntegrationController(AuthCommonService authCommonService, MerchantAuthService merchantAuthService,
                                         MerchantTransactionBO merchantTransactionBO, MerchantUserBO merchantUserBO, MerchantEligibilitiesBO merchantEligibilitiesBO) {
        this.authCommonService = authCommonService;
        this.merchantAuthService = merchantAuthService;
        this.merchantTransactionBO = merchantTransactionBO;
        this.merchantUserBO = merchantUserBO;
        this.merchantEligibilitiesBO = merchantEligibilitiesBO;
    }

    @PostMapping("/secure/api/v1/offers")
    public Map<String, Map<String, List<MerchantPriceResponse>>> getOffers(
            @Validated @RequestBody MerchantOfferRequest request,
            HttpServletRequest httpServletRequest) {

        LOGGER.info("Received get merchant offers request with parameters: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        merchantAuthService.authenticate(
                MerchantAuthDto.builder()
                        .request(httpServletRequest)
                        .source(MerchantAuthSource.MERCHANT)
                        .merchantIdOrDisplayId(displayMerchantId)
                        .build()
        );
        return merchantTransactionBO.getOffers(displayMerchantId, request, false, false);
    }

    @PostMapping("/secure/internal/api/v1/eligible-offers")
    public MerchantEligibleOfferResponse getEligibleOffersInternal(
            @Validated @RequestBody MerchantOfferRequest request,
            HttpServletRequest httpServletRequest) {

        LOGGER.info("Received internal get merchant eligible offers request with parameters: {}", request);
        merchantAuthService.authenticate(
                MerchantAuthDto.builder()
                        .request(httpServletRequest)
                        .source(MerchantAuthSource.INTERNAL)
                        .merchantIdOrDisplayId(request.getMerchantId())
                        .build()
        );
        return merchantTransactionBO.getMerchantOffersInternal(request.getMerchantId(), request, true, true);
    }

    //juspay migration
    @PostMapping("/api/v1/checkEligibility")
    public CheckEligibilityResponse checkEligibility(@Valid @RequestBody CheckEligibilityRequest request,
                                                     HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to check eligibility with params: {}", request);
        try {

            String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
            MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
            LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
            return merchantEligibilitiesBO.checkEligibility(mu, request,
                    EligibilityApiType.MERCHANT_ELIGIBILITY_API.name());
        } catch (Exception e) {
            LOGGER.error("Exception while checking eligibility: ", e);
            throw new FreewayException("Something went wrong!", "merchant", null != request ? request.getEmail() : "");
        }
    }

    @PostMapping("/api/v1/eligible-offers")
    public Map<String, Map<String, List<MerchantPriceResponse>>> getEligibleOffers(
            @Validated @RequestBody MerchantOfferRequest request,
            HttpServletRequest httpServletRequest) {

        LOGGER.info("Received get merchant eligible offers request with parameters: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        merchantAuthService.authenticate(
                MerchantAuthDto.builder()
                        .request(httpServletRequest)
                        .source(MerchantAuthSource.MERCHANT)
                        .merchantIdOrDisplayId(displayMerchantId)
                        .build()
        );
        return merchantTransactionBO.getOffers(displayMerchantId, request, true, false);
    }

    @PostMapping("/api/v2/transactions")
    public PgTransactionResponse createPgTransaction(@Valid @RequestBody PgTransactionRequest request,
                                                     @RequestHeader(value = "X-Real-IP", required = false)
                                                     String sourceIp, HttpServletRequest httpServletRequest) {
        request.setIp(sourceIp);
        LOGGER.info("Received create pg transaction request with params: {}", request);

        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Received create pg transaction request with params: {} from merchant: {}", request,
                displayMerchantId);
        return merchantTransactionBO.createTransaction(request, mu);
    }

    @GetMapping("/api/v1/transactions/{tid}")
    public TransactionResponse getTransactionById(@PathVariable("tid") String transactionId,
                                                  @RequestParam(value = "partner", required = false) String partner,
                                                  HttpServletRequest httpServletRequest) {
        LOGGER.info("Received request to get transaction by id for transaction id: {}", transactionId);
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchant = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Received request to get transaction by id for transaction id: {} from merchant: {} ",
                transactionId, merchant);
        return merchantTransactionBO.getTransactionByMerchantIdAndOrderId(merchant, transactionId);
    }

    @PostMapping("/api/v2/transactions/{paymentTxnId}/refunds")
    public RefundResponse createRefundTransaction(@PathVariable("paymentTxnId") String paymentTxnId,
                                                  @Valid @RequestBody RefundTransactionRequest refundTxnReq, HttpServletRequest httpServletRequest) {
        LOGGER.info("Refund request received for transaction id: {} with params: {}", paymentTxnId, refundTxnReq);

        TransactionResponse tr = merchantTransactionBO.processRefundTransaction(paymentTxnId, refundTxnReq);
        return tr.getRefund();
    }

    @GetMapping("/api/v1/profile/constants")
    public ConsumerProfileConstants getProfileConstants() {
        return merchantTransactionBO.getProfileConstant();
    }
}
