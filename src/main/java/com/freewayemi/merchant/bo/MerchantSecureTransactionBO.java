package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.deliveryorder.DeliveryOrderResp;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutRequest;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.entity.SecurityCredentials;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.commons.utils.EncryptionUtil;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.PaymentProviderTransactionResponseV2;
import com.freewayemi.merchant.dto.PostPaymentResponse;
import com.freewayemi.merchant.dto.ValidateOtpRequest;
import com.freewayemi.merchant.dto.request.PartnerInfoResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.utils.MerchantCommonUtil;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.freewayemi.merchant.utils.Constants.TRANSACTION_NOT_FOUND;

@Component
public class MerchantSecureTransactionBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSecureTransactionBO.class);

    private final MerchantTransactionBO merchantTransactionBO;
    private final MerchantUserBO merchantUserBO;

    @Autowired
    public MerchantSecureTransactionBO(MerchantTransactionBO merchantTransactionBO, MerchantUserBO merchantUserBO) {
        this.merchantTransactionBO = merchantTransactionBO;
        this.merchantUserBO = merchantUserBO;
    }

    public SecureTransactionResponse initiate(MerchantUser mu, SecureTransactionRequest request, String partner) {
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        if (StringUtils.hasText(partner)){
            securityCredentials = merchantUserBO.updateSecurityCredentialsWithTenant(mu, partner);
        }

        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        PgTransactionRequest pgTransactionRequest;
        try {
            pgTransactionRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    PgTransactionRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        PgTransactionResponse pgTransactionResponse =
                merchantTransactionBO.createPgTransaction(mu, pgTransactionRequest, false,
                        TransactionSource.secureApi.name());
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(pgTransactionResponse, securityCredentials))
                .build();
    }

    public ResponseEntity<?> inquiry(MerchantUser mu, String orderIdOrTransactionId, String partner) {
        try {
            // checking whether online inquiry is enabled or not
            MerchantCommonUtil.isOnlineInquiryEnabled(mu, orderIdOrTransactionId);
            SecurityCredentials securityCredentials = mu.getSecurityCredentials();
            if (StringUtils.hasText(partner)){
                securityCredentials = merchantUserBO.updateSecurityCredentialsWithTenant(mu, partner);
            }
            TransactionResponse tr =
                    merchantTransactionBO.getTransactionByMerchantIdAndOrderId(String.valueOf(mu.getId()),
                            orderIdOrTransactionId);
            if (String.valueOf(mu.getId()).equals(tr.getMerchantId()) || String.valueOf(mu.getId()).equals(tr.getMasterMerchants())) {
                TransactionV2Response transactionV2Response = new TransactionV2Response(tr);
                String encryptedResp = getEncryptedString(transactionV2Response, securityCredentials);
                return new ResponseEntity<>(SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build(), HttpStatus.OK);
            }
            throw new FreewayException("Unauthorized access");
        } catch (FreewayException e) {
            LOGGER.error("FreewayException occurred while encrypting response: ", e);
            if (TRANSACTION_NOT_FOUND.equals(e.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Something went wrong!", "transaction", orderIdOrTransactionId);
        }
        throw new FreewayException("Something went wrong!", "transaction", orderIdOrTransactionId);
    }

    private String getDecryptedString(SecureTransactionRequest request, SecurityCredentials securityCredentials) {
        String decryptedRequest;
        try {
            decryptedRequest = EncryptionUtil.decrypt(request.getEncryptedRequest(), securityCredentials.getSecretKey(),
                    securityCredentials.getIvKey(), request.getSecretKeyType(), securityCredentials.getCipher());
            LOGGER.info("Decryption successful for request: {}", request);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while decrypting request: ", e);
            throw new FreewayException("Unable to decrypt request");
        }
        return decryptedRequest;
    }

    private String getEncryptedString(Object objectToBeConverted, SecurityCredentials securityCredentials) {
        String encryptedResp;
        try {
            String secretKeyType = StringUtils.isEmpty(securityCredentials.getSecretKeyType()) ? null
                    : securityCredentials.getSecretKeyType();
            encryptedResp = EncryptionUtil.encrypt(Util.convertToString(objectToBeConverted, false),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), secretKeyType,
                    securityCredentials.getCipher());
            LOGGER.info("Initiate response for initiate transaction is: {} ", encryptedResp);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Unable to encrypting response");
        }
        return encryptedResp;
    }

    public SecureTransactionResponse processRefundTransaction(MerchantUser mu, String paymentTxnId,
                                                              SecureTransactionRequest request, String partner) {
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        RefundTransactionRequest refundTransactionRequest;
        if (StringUtils.hasText(partner)){
            securityCredentials = merchantUserBO.updateSecurityCredentialsWithTenant(mu, partner);
        }
        try {
            refundTransactionRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    RefundTransactionRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request: ", e);
            throw new FreewayException("Incorrect request body");
        }
        TransactionResponse tr = merchantTransactionBO.processRefundTransaction(paymentTxnId, refundTransactionRequest);
        RefundResponse refundResponse = tr.getRefund();
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(refundResponse, securityCredentials))
                .build();
    }

    public SecureTransactionResponse createPaymentLink(MerchantUser mu, SecureTransactionRequest request) {
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        PgTransactionRequest pgTransactionRequest;
        try {
            pgTransactionRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    PgTransactionRequest.class);
            pgTransactionRequest.setPartner(mu.getPartner());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        PaymentLinkResponse paymentLinkResponse = merchantTransactionBO.createPaymentLink(mu, pgTransactionRequest);
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(paymentLinkResponse, securityCredentials))
                .build();
    }

    public SecureTransactionResponse getPaymentLink(MerchantUser mu, String orderId) {
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        PaymentLinkResponse paymentLinkResponse =
                merchantTransactionBO.getPaymentLink(String.valueOf(mu.getId()), orderId);
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(paymentLinkResponse, mu.getSecurityCredentials()))
                .build();
    }

    public SecureTransactionResponse createRefundAsPayout(MerchantUser mu, SecureTransactionRequest request,
                                                          String source) {
        RefundPayoutResponse rpr;
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            rpr = RefundPayoutResponse.builder()
                    .statusCode(TransactionCode.FAILED_115.getCode())
                    .status(TransactionCode.FAILED_115.getStatus())
                    .statusMessage(TransactionCode.FAILED_115.getStatusMsg())
                    .build();
            return SecureTransactionResponse.builder()
                    .encryptedResponse(getEncryptedString(rpr, mu.getSecurityCredentials()))
                    .build();
        }
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        RefundPayoutRequest refundPayoutRequest;
        try {
            refundPayoutRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    RefundPayoutRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to refundPayoutRequest object: ", e);
            rpr = RefundPayoutResponse.builder()
                    .statusCode(TransactionCode.FAILED_116.getCode())
                    .status(TransactionCode.FAILED_116.getStatus())
                    .statusMessage(TransactionCode.FAILED_116.getStatusMsg())
                    .build();
            return SecureTransactionResponse.builder()
                    .encryptedResponse(getEncryptedString(rpr, mu.getSecurityCredentials()))
                    .build();
        }
        rpr = merchantTransactionBO.createRefundAsPayout(mu, refundPayoutRequest, source);
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(rpr, securityCredentials))
                .build();
    }

    public SecureTransactionResponse createPgTransaction(MerchantUser mu, SecureTransactionRequest request, String partner) {
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        if (StringUtils.hasText(partner)){
            securityCredentials = merchantUserBO.updateSecurityCredentialsWithTenant(mu, partner);
        }
        PgTransactionRequest pgTransactionRequest;
        try {
            pgTransactionRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    PgTransactionRequest.class);
            if(StringUtils.hasText(partner)){
                pgTransactionRequest.setPartner(partner);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        PgTransactionResponse pgTransactionResponse =
                merchantTransactionBO.createPgTransaction(mu, pgTransactionRequest, true,
                        TransactionSource.secureSeamplessApi.name());
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(pgTransactionResponse, securityCredentials))
                .build();
    }

    public SecureTransactionResponse getDeliveryOrder(MerchantUser mu, String orderIdOrpaymentTxnId) {
        try {
            SecurityCredentials securityCredentials = mu.getSecurityCredentials();
            DeliveryOrderResp deliveryOrder = merchantTransactionBO.getDeliveryOrder(mu, orderIdOrpaymentTxnId);
            if (Util.isNotNull(deliveryOrder)) {
                String encryptedResp = getEncryptedString(deliveryOrder, securityCredentials);
                return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
            }
            throw new FreewayException("Unauthorized access");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Something went wrong!", "transaction", orderIdOrpaymentTxnId);
        }
    }

    public SecureTransactionResponse createSeamlessTransactionV2(MerchantUser mu, SecureTransactionRequest request) {
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        PgTransactionRequest pgTransactionRequest;
        try {
            pgTransactionRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                    PgTransactionRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        PgTransactionResponse pgTransactionResponse =
                merchantTransactionBO.createSeamlessTransactionV2(mu, pgTransactionRequest, true,
                        TransactionSource.secureSeamplessApi.name(), false);
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(pgTransactionResponse, securityCredentials))
                .build();
    }

    public SecureTransactionResponse payPayment(SecureTransactionRequest request, MerchantUser mu, String transactionId, String consumerId) {
        try {
            SecurityCredentials securityCredentials = mu.getSecurityCredentials();
            PgConsumerPaymentRequest pgConsumerPaymentRequest;
            try {
                pgConsumerPaymentRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                        PgConsumerPaymentRequest.class);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while converting request to pgConsumerPaymentRequest object: ", e);
                throw new FreewayException("Incorrect request body");
            }
            PaymentProviderTransactionResponseV2 paymentProviderTransactionResponseV2 =
                    merchantTransactionBO.payPayment(pgConsumerPaymentRequest, transactionId, consumerId);
            if (Util.isNotNull(paymentProviderTransactionResponseV2)) {
                String encryptedResp = getEncryptedString(paymentProviderTransactionResponseV2, securityCredentials);
                return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
            }
            throw new FreewayException("Unauthorized access");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Something went wrong!", "transaction", transactionId);
        }
    }

    public SecureTransactionResponse validateOtp(SecureTransactionRequest request, MerchantUser mu, String paymentTxnId) {
        try {
            SecurityCredentials securityCredentials = mu.getSecurityCredentials();
            ValidateOtpRequest validateOtpRequest;
            try {
                validateOtpRequest = Util.convertToJsonObject(getDecryptedString(request, securityCredentials),
                        ValidateOtpRequest.class);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while converting request to validateOtp object: ", e);
                throw new FreewayException("Incorrect request body");
            }
            PostPaymentResponse postPaymentResponse = merchantTransactionBO.validateOtp(validateOtpRequest, paymentTxnId);
            if (Util.isNotNull(postPaymentResponse)) {
                String encryptedResp = getEncryptedString(postPaymentResponse, securityCredentials);
                return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
            }
            throw new FreewayException("Unauthorized access");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Something went wrong!", "transaction", paymentTxnId);
        }
    }

    public SecureTransactionResponse claimProduct(MerchantUser mu, SecureTransactionRequest request) {
        AsyncClaimRequest asyncClaimRequest;
        try {
            asyncClaimRequest = Util.convertToJsonObject(getDecryptedString(request, mu.getSecurityCredentials()),
                    AsyncClaimRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to asyncClaimRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        AsyncClaimResponse asyncClaimResponse = merchantTransactionBO.asyncClaim(asyncClaimRequest, mu);
        return SecureTransactionResponse.builder()
                .encryptedResponse(getEncryptedString(asyncClaimResponse, mu.getSecurityCredentials()))
                .build();
    }

}
