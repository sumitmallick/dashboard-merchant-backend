package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentLinkRequest extends PgTransactionRequest {

    private final String merchantId;
    private final String shopName;
    private final Boolean sendSmsShortLink;
    private final Boolean sendEmailShortLink;
    // expiry time in minutes
    private final Integer expiryTime;

    @JsonCreator
    public PaymentLinkRequest(@JsonProperty("amount") Float amount, @JsonProperty("mobile") String mobile,
                              @JsonProperty("orderId") String orderId, @JsonProperty("email") String email,
                              @JsonProperty("productName") String productName,
                              @JsonProperty("productId") String productId,
                              @JsonProperty("productSkuCode") String productSkuCode,
                              @JsonProperty("consumerName") String consumerName,
                              @JsonProperty("address") Address address,
                              @JsonProperty("customParams") Map<String, String> customParams,
                              @JsonProperty("merchantId") String merchantId, @JsonProperty("shopName") String shopName,
                              @JsonProperty("iframe") Boolean iframe, @JsonProperty("firstName") String firstName,
                              @JsonProperty("middleName") String middleName, @JsonProperty("lastName") String lastName,
                              @JsonProperty("returnUrl") String returnUrl,
                              @JsonProperty("webhookUrl") String webhookUrl,
                              @JsonProperty("sendPaymentLink") Boolean sendPaymentLink,
                              @JsonProperty("sendSmsShortLink") Boolean sendSmsShortLink,
                              @JsonProperty("sendEmailShortLink") Boolean sendEmailShortLink,
                              @JsonProperty("products") List<PaymentTransactionRequest.ProductInfo> products,
                              @JsonProperty("maxTenure") Integer maxTenure, @JsonProperty("pan") String pan,
                              @JsonProperty("dob") String dob, @JsonProperty("annualIncome") String annualIncome,
                              @JsonProperty("cardData") CardData cardData, @JsonProperty("tenure") Integer tenure,
                              @JsonProperty("cardInfo") CardInfo cardInfo,
                              @JsonProperty("isSubvention") Boolean isSubvention,
                              @JsonProperty("subventionAmount") String subventionAmount,
                              @JsonProperty("gender") String gender,
                              @JsonProperty("partner") String partner,
                              @JsonProperty("expiryTime") Integer expiryTime,
                              @JsonProperty("providerGroup") String providerGroup) {
        super(amount, mobile, orderId, email, productName, productId, productSkuCode, consumerName, address, customParams, iframe,
                firstName, middleName, lastName, returnUrl, webhookUrl, sendPaymentLink, cardData, tenure, cardInfo, products,
                maxTenure, pan, dob, null, null, annualIncome, isSubvention, subventionAmount, gender, partner, providerGroup);
        this.merchantId = merchantId;
        this.shopName = shopName;
        this.sendSmsShortLink = sendSmsShortLink;
        this.sendEmailShortLink = sendEmailShortLink;
        this.expiryTime = expiryTime;
    }
}
