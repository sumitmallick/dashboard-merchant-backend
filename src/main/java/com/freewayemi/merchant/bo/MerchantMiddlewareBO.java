package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.MerchantMiddlewareService;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class MerchantMiddlewareBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantMiddlewareBO.class);
    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final MerchantMiddlewareService mms;
    private final PaymentServiceBO paymentServiceBO;

    @Autowired
    public MerchantMiddlewareBO(MerchantMiddlewareService mms, PaymentServiceBO paymentServiceBO) {
        this.mms = mms;
        this.paymentServiceBO = paymentServiceBO;
    }

    public AsyncClaimResponse asyncVerifyAndClaim(AsyncClaimRequest request, MerchantUser mu) {
        validateAsyncVerifyAndClaimRequest(request);
        TransactionResponse transaction = getSuccessBrandTransaction(request, mu);
        if (Objects.nonNull(mu) && Objects.nonNull(mu.getParams()) &&
                org.springframework.util.StringUtils.hasText(mu.getParams().getAsyncClaimTATInDays())){
            String asyncTatDays = mu.getParams().getAsyncClaimTATInDays();
            Instant txnDate = DateUtil.convertUtcDateWithPattern(transaction.getTxnSuccessDate(), dateFormat);
            if (DateUtil.getDaysBetweenTwoDates(txnDate, Instant.now()) > Long.parseLong(asyncTatDays)){
                return AsyncClaimResponse.builder()
                        .result(Boolean.FALSE)
                        .code(MerchantResponseCode.SERIAL_NUMBER_CLAIM_FAILED.getCode())
                        .message(MerchantResponseCode.SERIAL_NUMBER_CLAIM_FAILED.getFormattedMessage(asyncTatDays))
                        .build();
            }
        }

        boolean claimSuccess = false;
        boolean isCallRequired = isCallRequired(transaction);
        if (isCallRequired) {
            ProductSkuResponse productSkuResponse = asyncVerifyAndClaim(transaction, request.getSerialNumber());
            if (productSkuResponse == null) {
                throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
            }
            claimSuccess = ProductSkuResponseStatus.CLAIMED.name().equalsIgnoreCase(productSkuResponse.getStatus()) ||
                    ProductSkuResponseStatus.VALID.name().equalsIgnoreCase(productSkuResponse.getStatus());
        }

        boolean isSerialNumberValidationSuccess = claimSuccess || !isCallRequired;
        if (isSerialNumberValidationSuccess) {
            updateTransactionSerialNumber(transaction.getTxnId(), request.getSerialNumber());
        }

        // need all middleware response codes?
        MerchantResponseCode responseCode = isSerialNumberValidationSuccess ?
                MerchantResponseCode.SERIAL_NUMBER_CLAIMED :
                MerchantResponseCode.SERIAL_NUMBER_FAILED;

        return AsyncClaimResponse.builder()
                .result(isSerialNumberValidationSuccess)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .build();
    }

    private void updateTransactionSerialNumber(String transactionId, String serialNumber) {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .updateType(UpdateTransactionType.SERIAL_NUMBER.name())
                .serialNumber(serialNumber)
                .build();
        try {
            UpdateTransactionResponse response = paymentServiceBO.updateTransaction(transactionId, request);
            if (response == null || !"success".equalsIgnoreCase(response.getStatus())) {
                LOGGER.error("Updating serial number in transaction: {} failed with response: {}", transactionId, response);
                return;
            }
            LOGGER.info("Updated transaction: {} with serial number for async verify and claim", transactionId);
        } catch (Exception e) {
            LOGGER.error("Updating serial number in transaction failed for transaction: " + transactionId, e);
        }
    }

    private static void validateAsyncVerifyAndClaimRequest(AsyncClaimRequest request) {
        if (StringUtils.isBlank(request.getTransactionId())) {
            throw new MerchantException(MerchantResponseCode.TRANSACTION_REQUIRED);
        }
        if (StringUtils.isBlank(request.getSerialNumber())) {
            throw new MerchantException(MerchantResponseCode.SERIAL_NUMBER_REQUIRED);
        }
    }

    private TransactionResponse getSuccessBrandTransaction(AsyncClaimRequest request, MerchantUser mu) {
        TransactionResponse transaction = getTransaction(request, mu);

        if (!TransactionStatus.success.name().equalsIgnoreCase(transaction.getStatus())) {
            throw new MerchantException(MerchantResponseCode.TRANSACTION_NOT_SUCCESS);
        }
        if (!isBrandTransaction(transaction)) {
            throw new MerchantException(MerchantResponseCode.SERIAL_NUMBER_VALIDATION_NOT_REQUIRED);
        }
        if (StringUtils.isNotBlank(transaction.getAdditionInfo().getBrandInfo().getSerialNumber())) {
            throw new MerchantException(MerchantResponseCode.SERIAL_NUMBER_ALREADY_CLAIMED);
        }
        return transaction;
    }

    private boolean isBrandTransaction(TransactionResponse transaction) {
        return transaction != null && transaction.getAdditionInfo() != null &&
                transaction.getAdditionInfo().getBrandInfo() != null;
    }

    private Boolean isCallRequired(TransactionResponse transaction) {
        return isCallRequiredForNTB(transaction) ||
                (null != transaction.getAdditionInfo() && null != transaction.getAdditionInfo().getBrandInfo() &&
                        StringUtils.isNotBlank(transaction.getAdditionInfo().getBrandInfo().getEmiOption()) &&
                        (null != transaction.getCashbackCharges() && !transaction.getCashbackCharges().equals(0.0f) ||
                                isBrandAdditionalCashbackValidationModelEnabled(transaction)));
    }

    private boolean isCallRequiredForNTB(TransactionResponse transaction) {
        return CardTypeEnum.NTB.getCardType().equalsIgnoreCase(transaction.getCardType()) &&
                Objects.nonNull(transaction.getAdditionInfo()) &&
                Objects.nonNull(transaction.getAdditionInfo().getBrandInfo()) &&
                StringUtils.isNotBlank(transaction.getAdditionInfo().getBrandInfo().getEmiOption()) &&
                StringUtils.isNotBlank(transaction.getAdditionInfo().getBrandInfo().getProductSkuId());
    }

    private static boolean isBrandAdditionalCashbackValidationModelEnabled(TransactionResponse transaction) {
        return null != transaction.getAdditionInfo() && null != transaction.getAdditionInfo().getBrandInfo() &&
                null != transaction.getAdditionInfo().getBrandInfo().getIsBrandAdditionalCashbackValidationModel() &&
                transaction.getAdditionInfo().getBrandInfo().getIsBrandAdditionalCashbackValidationModel() &&
                null != transaction.getAdditionalCashbackAmount() && transaction.getAdditionalCashbackAmount() > 0.0f;
    }

    private TransactionResponse getTransaction(AsyncClaimRequest request, MerchantUser mu) {
        TransactionResponse transaction;
        try {
            transaction = paymentServiceBO.getTransactionById(request.getTransactionId());
        } catch (Exception e) {
            LOGGER.error("Error while fetching transaction");
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
        }
        if (transaction == null || !mu.getId().toString().equals(transaction.getMerchantId())) {
            throw new MerchantException(MerchantResponseCode.INVALID_TRANSACTION);
        }
        return transaction;
    }

    private ProductSkuResponse asyncVerifyAndClaim(TransactionResponse transaction, String serialNumber) {
        return mms.asyncVerifyAndClaim(generatePostUpdateProductSkuRequest(transaction, serialNumber),
                transaction.getAdditionInfo().getBrandInfo().getProductSkuId());
    }

    private UpdateProductSkuRequest generatePostUpdateProductSkuRequest(TransactionResponse transaction,
                                                                       String serialNumber) {
        return UpdateProductSkuRequest.builder()
                .merchantId(transaction.getMerchantId())
                .productId(transaction.getAdditionInfo().getBrandInfo().getBrandProductId())
                .serialNumber(serialNumber)
                .modelNumber(transaction.getAdditionInfo().getBrandInfo().getModelNumber())
                .consumerId(transaction.getConsumerId())
                .consumerMobile(transaction.getMobile())
                .brand(modifyBrandName(transaction.getAdditionInfo().getBrandInfo().getName()))
                .status(transaction.getStatus())
                .stage("postpayment")
                .transactionId(transaction.getTxnId())
                .offerTenure(transaction.getTenure())
                .advanceEmiTenure(transaction.getAdvanceEmiTenure())
                .bank(transaction.getBankName())
                .paymentInfo(createPaymentInfo(transaction))
                .build();
    }

    private PaymentInfo createPaymentInfo(TransactionResponse transaction) {
        float dbdRate = (Objects.nonNull(transaction.getDbd()) ?
                transaction.getDbd().floatValue() : 0.0f);
        float cashback = Objects.nonNull(transaction.getCashbackCharges()) ?
                transaction.getCashbackCharges() : 0.0f;
        float cashbackRate = Objects.nonNull(transaction.getPgAmount()) &&
                transaction.getPgAmount() > 0.0f && cashback > 0.0f
                ? cashback / transaction.getPgAmount() * 100.0f : 0.0f;
        float dbdAmount = (Objects.nonNull(transaction.getDbdAmount()) ?
                transaction.getDbdAmount().floatValue() : 0.0f);

        return PaymentInfo.builder()
                .bank(transaction.getBankName())
                .offerTenure(transaction.getTenure())
                .advanceEmiTenure(transaction.getAdvanceEmiTenure())
                .cardType(transaction.getCardType())
                .irr(transaction.getIrr())
                .loanAmount(transaction.getPgAmount())
                .dbdAmount(Math.min(dbdRate, cashbackRate))
                .dbdRate(Math.min(dbdAmount, cashback))
                .mbdRate(Math.max(cashbackRate - dbdRate, 0.0f))
                .mbdAmount(Math.max(cashback - dbdAmount, 0.0f))
                .processingFee(transaction.getProcessingFee())
                .gstOnProcessingFee(transaction.getGstOnProcessingFee())
                .build();
    }

    private String modifyBrandName(String brand) {
        return brand != null ? brand.replaceAll(" ", "").toUpperCase() : "";
    }
}
