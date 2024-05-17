package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PgTransactionRequest {

    @NotNull(message = "Please provide amount.")
    private final Float amount;

    @NotNull(message = "Please provide mobile.")
    private final String mobile;

    @NotNull(message = "Please provide order")
    private final String orderId;

    @Email(message = "Email must be a valid email address")
    private final String email;

    private final String productName;
    private String productId;
    private final String productSkuCode;
    private final String consumerName;
    private final Address address;
    private Map<String, String> customParams;
    private final Boolean iframe;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String returnUrl;
    private String webhookUrl;
    private final Boolean sendPaymentLink;
    private final CardData cardData;
    private final Integer tenure;
    private final CardInfo cardInfo;
    private final List<PaymentTransactionRequest.ProductInfo> products;
    private final Integer maxTenure;
    private final String pan;
    private final String dob;
    private final String annualIncome;
    private final String latitude;
    private final String longitude;
    private String ip;
    private final Boolean isSubvention;
    private final String subventionAmount;
    private final String gender;
    private String partner;
    private final String providerGroup;

    @JsonCreator
    public PgTransactionRequest(@JsonProperty("amount") Float amount, @JsonProperty("mobile") String mobile,
                                @JsonProperty("orderId") String orderId,
                                @JsonProperty("email") String email,
                                @JsonProperty("productName") String productName,
                                @JsonProperty("productId") String productId,
                                @JsonProperty("productSkuCode") String productSkuCode,
                                @JsonProperty("consumerName") String consumerName,
                                @JsonProperty("address") Address address,
                                @JsonProperty("customParams") Map<String, String> customParams,
                                @JsonProperty("iframe") Boolean iframe,
                                @JsonProperty("firstName") String firstName,
                                @JsonProperty("middleName") String middleName,
                                @JsonProperty("lastName") String lastName,
                                @JsonProperty("returnUrl") String returnUrl,
                                @JsonProperty("webhookUrl") String webhookUrl,
                                @JsonProperty("sendPaymentLink") Boolean sendPaymentLink,
                                @JsonProperty("cardData") CardData cardData, @JsonProperty("tenure") Integer tenure,
                                @JsonProperty("cardInfo") CardInfo cardInfo,
                                @JsonProperty("products") List<PaymentTransactionRequest.ProductInfo> products,
                                @JsonProperty("maxTenure") Integer maxTenure, @JsonProperty("pan") String pan,
                                @JsonProperty("dob") String dob,
                                @JsonProperty("latitude") String latitude, @JsonProperty("longitude") String longitude,
                                @JsonProperty("annualIncome") String annualIncome,
                                @JsonProperty("isSubvention") Boolean isSubvention,
                                @JsonProperty("subventionAmount") String subventionAmount,
                                @JsonProperty("gender") String gender,
                                @JsonProperty("partner") String partner,
                                @JsonProperty("providerGroup") String providerGroup) {
        this.amount = amount;
        this.mobile = Util.formatMobile(mobile);
        this.orderId = orderId;
        this.email = email;
        this.productName = productName;
        this.productId = productId;
        this.productSkuCode = productSkuCode;
        this.middleName = middleName;
        this.products = products;
        this.consumerName = consumerName;
        this.address = address;
        this.customParams = customParams;
        this.iframe = iframe;
        this.firstName = firstName;
        this.lastName = lastName;
        this.returnUrl = returnUrl;
        this.webhookUrl = webhookUrl;
        this.sendPaymentLink = sendPaymentLink;
        this.cardData = cardData;
        this.tenure = tenure;
        this.cardInfo = cardInfo;
        this.maxTenure = maxTenure;
        this.pan = pan;
        this.dob = dob;
        this.latitude = latitude;
        this.longitude = longitude;
        this.annualIncome = annualIncome;
        this.isSubvention = isSubvention;
        this.subventionAmount = subventionAmount;
        this.gender = gender;
        this.partner = partner;
        this.providerGroup = providerGroup;
    }
}
