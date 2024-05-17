package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantTransactionBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.deliveryorder.DeliveryOrderResp;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutRequest;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundInquiryResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.PricingOptionsRequest;
import com.freewayemi.merchant.dto.response.ConsumerProfileConstants;
import com.freewayemi.merchant.dto.response.EnquiryTransactionResponse;
import com.freewayemi.merchant.dto.sales.MerchantTransactionVolumeRequest;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.repository.PartnerRepository;
import com.freewayemi.merchant.service.*;
import com.freewayemi.merchant.utils.MerchantCommonUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class MerchantTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantTransactionController.class);

    private final MerchantTransactionBO merchantTransactionBO;
    private final MerchantUserBO merchantUserBO;
    private final AuthCommonService authCommonService;
    private final PartnerRepository partnerRepository;
    private final MerchantUserRepository merchantUserRepository;
    private final PaymentServiceBO paymentServiceBO;
    private final SalesAgentService salesAgentService;
    private final SalesMSService salesMSService;
    private final RuleEngineHelperService ruleEngineHelperService;
    private final NotificationService notificationService;
    private final PaymentOpsService paymentOpsService;

    @Autowired
    public MerchantTransactionController(MerchantTransactionBO merchantTransactionBO, MerchantUserBO merchantUserBO,
                                         AuthCommonService authCommonService,
                                         PartnerRepository partnerRepository,
                                         MerchantUserRepository merchantUserRepository, SalesAgentService salesAgentService,
                                         SalesMSService salesMSService, RuleEngineHelperService ruleEngineHelperService,
                                         PaymentServiceBO paymentServiceBO, NotificationService notificationService,
                                         PaymentOpsService paymentOpsService) {
        this.merchantTransactionBO = merchantTransactionBO;
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
        this.partnerRepository = partnerRepository;
        this.merchantUserRepository = merchantUserRepository;
        this.paymentServiceBO = paymentServiceBO;
        this.salesAgentService = salesAgentService;
        this.salesMSService = salesMSService;
        this.ruleEngineHelperService = ruleEngineHelperService;
        this.notificationService = notificationService;
        this.paymentOpsService = paymentOpsService;
    }

    @GetMapping("/api/v1/transactions")
    public List<TransactionResponse> myTransactions(@RequestParam(value = "mobile", required = false) String mobile,
                                                    @RequestParam(value = "partner", required = false) String partner,
                                                    HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();

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
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";
        if (StringUtils.hasText(storeUserId)) {
            return merchantTransactionBO.getStoreUserTransactions(storeUserId);
        }
        return merchantTransactionBO.getTransactionByMerchantId(merchant, mobile);
    }

    @GetMapping("/api/v1/settlements")
    public SettlementResponse mySettlements(@RequestParam(value = "partner", required = false) String partner,
                                            HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        if (Util.isNotNull(partner)) {
            return merchantTransactionBO.getSettlementsByMerchantId(merchant, partner);
        }
        return merchantTransactionBO.getSettlementsByMerchantId(merchant, null);
    }

    @PostMapping("/api/v1/transactions")
    public TransactionResponse initiateTransaction(@Valid @RequestBody TransactionRequest request,
                                                   @RequestParam(value = "partner", required = false) String partner,
                                                   @RequestHeader(value = "X-Real-IP", required = false)
                                                   String sourceIp, HttpServletRequest httpServletRequest,
                                                   @RequestHeader Map<String, String> headers) throws IOException {
        LOGGER.info("create transaction request: {}", request);
        if (request.getAmount() < 2000.0f) {
            throw new FreewayException("Minimum transaction amount should be 2000.0");
        }
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        MerchantUser merchantUser = merchantUserRepository.findById(merchant).get();
        if (Util.isNotNull(partner) && !partner.equals("")) {
            if (!ArrayUtils.isEmpty(merchantUser.getPartners().toArray()) && merchantUser.getPartners().contains(partner)) {
                MerchantUser merchantUserUpdated = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partner, false).get();
                if (Util.isNotNull(merchantUserUpdated)) {
                    merchantUser = merchantUserUpdated;
                }
            }
        }
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";
        if (Util.isNotNull(partner)) {
            TransactionResponse transactionResponse = merchantTransactionBO.initiate(merchantUser.getId().toString(), request, sourceIp, storeUserId, partner);
            ruleEngineHelperService.saveMobileData(headers, transactionResponse.getTxnId());
            return transactionResponse;
        }
        TransactionResponse transactionResponse = merchantTransactionBO.initiate(merchant, request, sourceIp, storeUserId, null);
        ruleEngineHelperService.saveMobileData(headers, transactionResponse.getTxnId());
        return transactionResponse;
    }

    @GetMapping("/api/v1/simulator")
    public List<PriceResponse> simulator(@RequestParam("price") Float price, HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received request on simulator for merchant id: {}", merchant);
        return merchantTransactionBO.simulate(merchant, price);
    }

    @GetMapping("/api/v2/simulator")
    public Map<String, List<PriceResponse>> simulator2(@RequestParam("price") Float price,
                                                       HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received request on simulator2 for merchant id: {}", merchant);
        return merchantTransactionBO.simulate2(merchant, price);
    }

    @GetMapping("/api/v2/transactions/{tid}")
    public TransactionV2Response transactionEnquiry(@PathVariable("tid") String orderIdOrTransactionId,
                                                    HttpServletRequest httpServletRequest) {
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Received transaction enquiry request for transaction id: {} and merchantId: {}",
                orderIdOrTransactionId, mu.getId());
        // checking whether online inquiry is enabled or not
        MerchantCommonUtil.isOnlineInquiryEnabled(mu, orderIdOrTransactionId);
        TransactionResponse tr = merchantTransactionBO.getTransactionByMerchantIdAndOrderId(String.valueOf(mu.getId()),
                orderIdOrTransactionId);
        if (String.valueOf(mu.getId()).equals(tr.getMerchantId())) {
            return new TransactionV2Response(tr);
        }
        throw new FreewayException("Unauthorized access");
    }

    @PostMapping("/api/v2/paymentLinks")
    public PaymentLinkResponse createPaymentLinks(@RequestBody PgTransactionRequest pgTransactionRequest,
                                                  HttpServletRequest httpServletRequest) {
        LOGGER.info("Create payment link request received for: {}", pgTransactionRequest);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        pgTransactionRequest.setPartner(mu.getPartner());
        return merchantTransactionBO.createPaymentLink(mu, pgTransactionRequest);
    }

    @GetMapping("/api/v2/paymentLinks/{orderId}")
    public PaymentLinkResponse getPaymentLinkStatus(@PathVariable("orderId") String orderId,
                                                    HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to get payment link status for orderId: {}", orderId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        String merchantId = String.valueOf(mu.getId());
        return merchantTransactionBO.getPaymentLink(merchantId, orderId);
    }

    @PostMapping("/api/v2/storeLinks")
    public StoreLinkResponse createStoreLinks(@RequestBody StoreLinkRequest storeLinkRequest,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to create store link for: {}", storeLinkRequest);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser merchantUser = merchantUserBO.getUserByDisplayId(displayMerchantId);
        return merchantTransactionBO.createStoreLink(merchantUser, storeLinkRequest);
    }

    @PostMapping("/api/v1/pay")
    public PgTransactionResponse createSeamlessPgTransaction(@Valid @RequestBody PgTransactionRequest request,
                                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Received seamless transaction request with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        return merchantTransactionBO.createPgTransaction(mu, request, true, TransactionSource.seamless.name());
    }

    @PostMapping("/api/v2/refundPayouts")
    public RefundPayoutResponse refundPayouts(@RequestBody RefundPayoutRequest request,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Refund payouts request received for: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Refund payouts request received from merchant display id: {}", displayMerchantId);
        return merchantTransactionBO.createRefundAsPayout(mu, request, TransactionSource.merchantPg.name());
    }

    @GetMapping("/api/v2/refundPayouts/{id}")
    public RefundPayoutResponse refundPayouts(@PathVariable("id") String id, HttpServletRequest httpServletRequest) {
        LOGGER.info("Received inquiry call for refund payout id: {}", id);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Received inquiry call for refund payout id: {} from merchant display id: {}", id,
                displayMerchantId);
        return null;
    }

    @PostMapping("/api/v3/transactions")
    public List<TransactionResponse> searchTransactions(@RequestParam(value = "partner", required = false) String partner,
                                                        @RequestBody TransactionSearchFilter transactionSearchFilter,
                                                        HttpServletRequest httpServletRequest) {
        LOGGER.info("Received request to search transactions with params: {}", transactionSearchFilter);
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received request to search transaction with params: {} from merchant: {}",
                transactionSearchFilter, merchantId);
        MerchantUser mu = merchantUserBO.getUserById(merchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchantId = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantTransactionBO.searchTransactions(merchantId, transactionSearchFilter);
    }

    @GetMapping("/api/v1/getDeliveryOrder/{orderId}")
    public DeliveryOrderResp getDeliveryOrder(@PathVariable("orderId") String orderIdOrpaymentTxnId,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Received getDeliveryOrder call for order id: {}", orderIdOrpaymentTxnId);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Received getDeliveryOrder call for orderIdOrpaymentTxnId: {} from merchant display id: {}",
                orderIdOrpaymentTxnId, displayMerchantId);
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        return merchantTransactionBO.getDeliveryOrder(mu, orderIdOrpaymentTxnId);
    }

    @PostMapping("/api/v1/pricing/options")
    public Map<String, List<PriceResponse>> getPricingOptions(@RequestParam(value = "partner", required = false) String partner,
                                                              @RequestBody PricingOptionsRequest pricingOptionsRequest,
                                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Received get pricing options request with parameters: {}", pricingOptionsRequest);
        if (pricingOptionsRequest.getAmount() < 2000.0f) {
            throw new FreewayException("Minimum transaction amount should be 2000.0");
        }
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser user = merchantUserBO.getUserById(merchantId);
        MerchantUser partnerUser = null;
        String userMobile = user.getMobile();
        List<String> partners = user.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                merchantId = partnerUser.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantTransactionBO.getPricingOptions(merchantId, pricingOptionsRequest);
    }

    @PostMapping("/api/v2/pay")
    public PgTransactionResponse createSeamlessTransactionV2(@Valid @RequestBody PgTransactionRequest request,
                                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Received seamless transaction request with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        return merchantTransactionBO.createSeamlessTransactionV2(mu, request, true, TransactionSource.seamless.name(), false);
    }

    @GetMapping("/api/v1/transactions/{paymentTxnId}/refunds/inquiry")
    public RefundInquiryResponse getRefundTransaction(@PathVariable("paymentTxnId") String paymentTxnId,
                                                      HttpServletRequest httpServletRequest) {
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByDisplayId(displayMerchantId);
        LOGGER.info("Refund Inquiry request received for displayMerchantId: {} and transaction id: {}",
                displayMerchantId, paymentTxnId);
        return merchantTransactionBO.getRefundTransaction(paymentTxnId, mu);
    }

    @PostMapping("/api/v1/expireTransactions")
    public PgExpireTransactionResponse expireTransaction(@Valid @RequestBody PgExpireTransactionRequest request,
                                                         @RequestParam(value = "partner", required = false) String partner,
                                                         HttpServletRequest httpServletRequest) {
        LOGGER.info("Received seamless transaction request with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                displayMerchantId = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return paymentServiceBO.expireTransaction(mu, request);
    }

    @GetMapping("/api/v1/transactions/getMerchantTransactions")
    public ResponseEntity<?> getMonthlyTransactionData(@RequestHeader(value = "X-API-KEY", required = false) String salesApiKey,
                                                       @RequestHeader(value = "X-AUTH-KEY", required = false) String salesAuthKey,
                                                       @RequestHeader(value = "X-SOURCE", required = false) String source,
                                                       @RequestParam("month") String month,
                                                       @RequestParam("year") String year,
                                                       @RequestParam(value = "partner", required = false) String partner,
                                                       @RequestParam(value = "skip", defaultValue = "0") Integer skip,
                                                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                       HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchantId = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        MerchantTransactionVolumeRequest merchantTransactionVolumeRequest = MerchantTransactionVolumeRequest.builder().merchantId(merchantId).month(month).year(year).build();
        return ResponseEntity.ok(salesMSService.getMerchantTransactions(merchantTransactionVolumeRequest));
    }

    @PostMapping("/api/v1/sendNotification")
    public void sendNotification(@Valid @RequestBody TransactionNotificationRequest request,
                                 HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchantId);
        if (Util.isNotNull(request) && !StringUtils.isEmpty(request.getConsumerMobile()) && !StringUtils.isEmpty(mu.getShopName())) {
            String consumerMobile = request.getConsumerMobile();
            notificationService.sendNTBTransactionNotification(consumerMobile, mu.getShopName());
            return;
        }
        throw new FreewayException("Consumer mobile number and merchant name should not null or empty string");
    }

    @GetMapping("/api/v1/transactions/getTransactionDO/{txnid}")
    public ResponseEntity<?> getTransactionDO(@RequestHeader(value = "X-API-KEY", required = false) String salesApiKey,
                                              @RequestHeader(value = "X-AUTH-KEY", required = false) String salesAuthKey,
                                              @RequestParam(value = "partner", required = false) String partner,
                                              @PathVariable("txnid") String transactionId,
                                              HttpServletRequest httpServletRequest) {
        if (!StringUtils.isEmpty(transactionId)) {
            return ResponseEntity.ok(paymentOpsService.getTransactionDO(transactionId));
        }
        throw new FreewayException("transactionId can't be null or empty");
    }

    //juspay ntb transaction enquiry
    @GetMapping("/api/v2/enquiry/{tid}")
    public ResponseEntity<?> getTransactionInfo(@PathVariable("tid") String transactionId,
                                                               @RequestParam(value = "partner", required = false) String partner,
                                                               HttpServletRequest httpServletRequest) {
        LOGGER.info("Received request to get transaction by id for transaction id: {}", transactionId);
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(merchant);
       return ResponseEntity.ok(merchantTransactionBO.getTransactionInfo(transactionId, partner, mu));
    }
}
