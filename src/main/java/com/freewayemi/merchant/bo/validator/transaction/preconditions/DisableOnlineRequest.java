package com.freewayemi.merchant.bo.validator.transaction.preconditions;

import com.freewayemi.merchant.commons.dto.PaymentTransactionRequest;
import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.entity.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Component
public class DisableOnlineRequest implements MerchantReqParamPreconditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisableOnlineRequest.class);

    private static final String SMPL = "SMPL_";
    private static final String COURSE = "Course :";
    private static final String SIMPLILEARN = "Simplilearn";
    private static final String HENRY_HARVIN = "Henry Harvin";
    private static final String SKILLOVILLA = "SKILLOVILLA";

    private final String disableOnlineMerchantDisplayIds;

    @Autowired
    public DisableOnlineRequest(
            @Value("${disable.online.merchant.display.ids}") String disableOnlineMerchantDisplayIds) {
        this.disableOnlineMerchantDisplayIds = disableOnlineMerchantDisplayIds;
    }

    @Override
    public void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException {
        MerchantUser merchantUser = (MerchantUser) object1;
        String displayId = merchantUser.getDisplayId();
        PgTransactionRequest pgTransactionRequest = (PgTransactionRequest) object2;
        String orderId = String.valueOf((pgTransactionRequest).getOrderId());
        // Validation to reject Simplilearn transactions basis order id validation
        if (disableOnlineMerchantDisplayIds.contains(displayId) && isSimplilearn(merchantUser.getShopName()) &&
                orderId.startsWith(SMPL)) {
            LOGGER.error(
                    "For displayId: {} and orderId: {} disabling online transaction as order id is starting with: {}",
                    displayId, orderId, SMPL);
            throw new FreewayException("Transactions disabled");
        }

        // Validation to reject Henry Harvin transactions basis product name
        if (disableOnlineMerchantDisplayIds.contains(displayId) && isHencryHarvin(merchantUser.getShopName())) {
            if (StringUtils.hasText(pgTransactionRequest.getProductName())) {
                validateProductName(displayId, orderId, pgTransactionRequest.getProductName());
            } else {
                if (Objects.nonNull(pgTransactionRequest.getProducts()) &&
                        !CollectionUtils.isEmpty(pgTransactionRequest.getProducts())) {
                    List<PaymentTransactionRequest.ProductInfo> products = pgTransactionRequest.getProducts();
                    for (PaymentTransactionRequest.ProductInfo productInfo : products) {
                        if (StringUtils.hasText(productInfo.getName())) {
                            validateProductName(displayId, orderId, productInfo.getName());
                        }
                    }
                }
            }
        }

        // Validation to reject Skillovilla transactions basis transaction source
        if (disableOnlineMerchantDisplayIds.contains(displayId) && isSkilloVilla(merchantUser.getShopName()) &&
                TransactionSource.merchantPg.name().equalsIgnoreCase(transactionSource)) {
            LOGGER.error("For displayId: {} and orderId: {} disabling online transaction as transaction source is: {}",
                    displayId, orderId, transactionSource);
            throw new FreewayException("Transactions disabled");
        }
    }

    private void validateProductName(String displayId, String orderId, String productName) {
        if (!productName.startsWith(COURSE)) {
            LOGGER.error(
                    "For displayId: {} and orderId: {} disabling online transaction as product name does not start " +
                            "with: {}", displayId, orderId, COURSE);
            throw new FreewayException("Transactions disabled");
        }
    }

    private boolean isSimplilearn(String shopName) {
        return StringUtils.hasText(shopName) && shopName.contains(SIMPLILEARN);
    }

    private boolean isHencryHarvin(String shopName) {
        return StringUtils.hasText(shopName) && shopName.contains(HENRY_HARVIN);
    }

    private boolean isSkilloVilla(String shopName) {
        return StringUtils.hasText(shopName) && shopName.contains(SKILLOVILLA);
    }

}
