package com.freewayemi.merchant.utils;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.CheckoutVersion;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.entity.MerchantUser;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

@Component
public class MerchantCommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantCommonUtil.class);

    private final String baseUrl;
    private final String transactionUrl;

    @Autowired
    public MerchantCommonUtil(@Value("${payment.base.url}") String baseUrl,
                              @Value("${payment.transaction.url}") String transactionUrl) {
        this.baseUrl = baseUrl;
        this.transactionUrl = transactionUrl;
    }

    public static Boolean isCheckoutV2(MerchantUser mu) {
        if (Util.isNotNull(mu) && Util.isNotNull(mu.getParams()) &&
                StringUtils.hasText(mu.getParams().getCheckoutVersion())) {
            return CheckoutVersion.V2.name().equalsIgnoreCase(mu.getParams().getCheckoutVersion());
        }
        return isDefaultCheckout(CheckoutVersion.V2);
    }

    private static boolean isDefaultCheckout(CheckoutVersion version) {
        return CheckoutVersion.DEFAULT.getVersion().equalsIgnoreCase(version.getVersion());
    }

    public String getPaymentRedirectionUrl(MerchantUser mu, String qrCode, String transactionId, String encryptedTxnId) {
        boolean isCheckoutV2 = isCheckoutV2(mu);
        boolean isCheckoutEDUV1 = isCheckoutEDUV1(mu);
        if(Objects.nonNull(mu) && Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getEncTxnLinkEnabled())){
            try {
                return  isCheckoutV2 ? baseUrl + "/cv3/#/etxn-status/" + URLEncoder.encode(encryptedTxnId, "UTF-8")
                        : isCheckoutEDUV1 ? baseUrl + String.format(paymentConstants.payment_EDU_URL_V1, transactionId)
                        : transactionUrl + transactionId;
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Error while encoding the encrypted transactionId: " + encryptedTxnId, e);
            }
        }
        return  isCheckoutV2 ? baseUrl + "/cv3/#/transaction/" + transactionId
                : isCheckoutEDUV1 ? baseUrl + String.format(paymentConstants.payment_EDU_URL_V1, transactionId)
                : transactionUrl + transactionId;
    }

    public static Boolean isCheckoutEDUV1(MerchantUser mu) {
        return Util.isNotNull(mu) && Util.isNotNull(mu.getParams()) &&
                StringUtils.hasText(mu.getParams().getCheckoutVersion()) &&
                CheckoutVersion.EDUV1.name().equalsIgnoreCase(mu.getParams().getCheckoutVersion());
    }

    public static void isOnlineInquiryEnabled(MerchantUser mu, String transactionId) {
        if (Objects.nonNull(mu) && Objects.nonNull(mu.getParams()) &&
                Objects.nonNull(mu.getParams().getIsOnlineInquiryEnabled()) &&
                BooleanUtils.isTrue(mu.getParams().getIsOnlineInquiryEnabled())) {
            LOGGER.error("Online inquiry is disabled for merchant display id: {} for transaction id: {}",
                    mu.getDisplayId(), transactionId);
            throw new FreewayException("Online inquiry is disabled");
        }
    }

    public static Boolean isNotEmptyString(String s){
        return !Objects.equals(s, "null")  && StringUtils.hasText(s);
    }

}
