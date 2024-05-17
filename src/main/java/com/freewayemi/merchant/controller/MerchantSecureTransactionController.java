package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantSecureTransactionBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.dto.SecureTransactionRequest;
import com.freewayemi.merchant.commons.dto.SecureTransactionResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.MerchantAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class MerchantSecureTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSecureTransactionController.class);

    private final MerchantSecureTransactionBO merchantSecureTransactionBO;
    private final MerchantUserBO merchantUserBO;

    private final AuthCommonService authCommonService;
    private final MerchantAuthService merchantAuthService;

    @Autowired
    public MerchantSecureTransactionController(MerchantSecureTransactionBO merchantSecureTransactionBO,
                                               MerchantUserBO merchantUserBO, AuthCommonService authCommonService,
                                               MerchantAuthService merchantAuthService) {
        this.merchantSecureTransactionBO = merchantSecureTransactionBO;
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
        this.merchantAuthService = merchantAuthService;
    }

    @PostMapping("/secure/api/v1/transactions")
    public SecureTransactionResponse initiateTransaction(@RequestBody SecureTransactionRequest request,
                                                         @RequestParam(value = "partner", required = false) String partner,
                                                         HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure transaction request received: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Request received from merchant display id: {}", displayMerchantId);
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantSecureTransactionBO.initiate(mu, request, null);
    }

    @GetMapping("/secure/api/v1/transactions/{tid}")
    public ResponseEntity<?> transactionInquiry(@PathVariable("tid") String orderIdOrTransactionId,
                                                @RequestParam(value = "partner", required = false) String partner,
                                                HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure inquiry transaction request received: {}", orderIdOrTransactionId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Secure transaction enquiry request for transaction id: {} and merchant display id: {}",
                orderIdOrTransactionId, mu.getDisplayId());
        return merchantSecureTransactionBO.inquiry(mu, orderIdOrTransactionId, null);
    }

    @PostMapping("/secure/api/v1/transactions/{paymentTxnId}/refunds")
    public SecureTransactionResponse createRefundTransaction(@PathVariable("paymentTxnId") String paymentTxnId,
                                                             @RequestParam(value = "partner", required = false) String partner,
                                                             @RequestBody SecureTransactionRequest request,
                                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure refund request received for transaction id: {} with params: {}", paymentTxnId, request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Secure transaction enquiry request for transaction id: {} and merchant display id: {}",
                paymentTxnId, mu.getDisplayId());
        return merchantSecureTransactionBO.processRefundTransaction(mu, paymentTxnId, request, null);
    }

    @PostMapping("/secure/api/v2/paymentLinks")
    public SecureTransactionResponse createPaymentLinks(@RequestBody SecureTransactionRequest request,
                                                        @RequestParam(value = "partner", required = false) String partner,
                                                        HttpServletRequest httpServletRequest) {
        LOGGER.info("Create payment link request received for: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Request received from merchant display id: {}", displayMerchantId);
        return merchantSecureTransactionBO.createPaymentLink(mu, request);
    }

    @GetMapping("/secure/api/v2/paymentLinks/{orderId}")
    public SecureTransactionResponse getPaymentLinkStatus(@PathVariable("orderId") String orderId,
                                                          @RequestParam(value = "partner", required = false) String partner,
                                                          HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to get payment link status for orderId: {}", orderId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantSecureTransactionBO.getPaymentLink(mu, orderId);
    }

    @PostMapping("/secure/api/v2/refundPayouts")
    public SecureTransactionResponse refundPayouts(@RequestBody SecureTransactionRequest request,
                                                   @RequestParam(value = "partner", required = false) String partner,
                                                   HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure Refund payouts request received for: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Secure Refund payouts request received from merchant display id: {}", displayMerchantId);
        return merchantSecureTransactionBO.createRefundAsPayout(mu, request, TransactionSource.secureApi.name());
    }

    @PostMapping("/secure/api/v1/pay")
    public SecureTransactionResponse createSeamlessTransaction(@Valid @RequestBody SecureTransactionRequest request,
                                                               @RequestParam(value = "partner", required = false) String partner,
                                                               HttpServletRequest httpServletRequest) {
        LOGGER.info("Received secure seamless transaction request with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Secure create pg transaction received from merchant display id: {}", displayMerchantId);
        return merchantSecureTransactionBO.createPgTransaction(mu, request, null);
    }

    @PostMapping("/secure/api/v2/pay")
    public SecureTransactionResponse createSeamlessTransactionV2(@Valid @RequestBody SecureTransactionRequest request,
                                                                 @RequestParam(value = "partner", required = false) String partner,
                                                                 HttpServletRequest httpServletRequest) {
        LOGGER.info("Received secure seamless transaction request with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Secure create pg transaction received from merchant display id: {}", displayMerchantId);
        return merchantSecureTransactionBO.createSeamlessTransactionV2(mu, request);
    }

    @GetMapping("/secure/api/v1/getDeliveryOrder/{orderId}")
    public SecureTransactionResponse getDeliveryOrder(@PathVariable("orderId") String orderIdOrpaymentTxnId,
                                                      @RequestParam(value = "partner", required = false) String partner,
                                                      HttpServletRequest httpServletRequest) {
        LOGGER.info("Received getDeliveryOrder call for order id: {}", orderIdOrpaymentTxnId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received getDeliveryOrder call for orderIdOrpaymentTxnId: {} from merchant display id: {}",
                orderIdOrpaymentTxnId, displayMerchantId);
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantSecureTransactionBO.getDeliveryOrder(mu, orderIdOrpaymentTxnId);
    }


    //move to tenant specific api's
    @PutMapping("/secure/api/v1/transactions/{tid}/process/{cid}")
    public SecureTransactionResponse payPayment(@PathVariable("tid") String transactionId,
                                                           @PathVariable("cid") String consumerId,
                                                           @Valid @RequestBody SecureTransactionRequest request,
                                                           HttpServletRequest httpServletRequest){
        LOGGER.info("Received pay payment call for transaction id: {} and consumer id: {}", transactionId, consumerId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received pay payment call for transaction id: {} and consumer id: {} from merchant display id: {}",
                transactionId, consumerId, displayMerchantId);
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return merchantSecureTransactionBO.payPayment(request, mu, transactionId, consumerId);
    }


    @PostMapping("/secure/api/v1/transactions/{tid}/validateOTP")
    public SecureTransactionResponse validateOtp(@PathVariable("tid") String paymentTxnId,
                                                 @Valid @RequestBody SecureTransactionRequest request,
                                                 HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received for validateOtp: {}", paymentTxnId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return merchantSecureTransactionBO.validateOtp(request, mu, paymentTxnId);
    }

    @PostMapping("/secure/api/v1/transactions/product/verifyAndClaim")
    public SecureTransactionResponse claimProduct(@RequestBody SecureTransactionRequest request,
                                                  @RequestParam(value = "partner", required = false) String partner,
                                                  HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure transaction request received: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Request received from merchant display id: {}", displayMerchantId);
        merchantAuthService.authenticate(
                MerchantAuthDto.builder()
                        .request(httpServletRequest)
                        .source(MerchantAuthSource.MERCHANT)
                        .merchantIdOrDisplayId(displayMerchantId)
                        .build()
        );
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantSecureTransactionBO.claimProduct(mu, request);
    }

}
