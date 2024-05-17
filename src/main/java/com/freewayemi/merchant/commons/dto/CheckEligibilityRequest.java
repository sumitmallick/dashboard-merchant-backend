package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckEligibilityRequest {

    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Please provide valid consumerName")
    private final String consumerName;

    @NotNull(message = "Please provide amount.")
    private final Float amount;

    @NotNull(message = "Please provide mobile.")
    private final String mobile;

    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "Please provide valid email.")
    private final String email;
    private final Address address;
    private final Map<String, String> customParams;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final CardData cardData;
    private final Integer tenure;
    private final CardInfo cardInfo;
    private final List<PaymentTransactionRequest.ProductInfo> products;
    private final String pan;
    private final String dob;
    private final String annualIncome;
    private String merchantId;
    private List<EligibilityResponse> eligibilityResponses;
    private List<PaymentProviderEnum> merchantSupportedProviders;
    private String source;

    private final String gender;
    private String partner;
    private final String providerGroup;

    @JsonCreator
    public CheckEligibilityRequest(@JsonProperty("consumerName") String consumerName,
                                   @JsonProperty("amount") Float amount, @JsonProperty("mobile") String mobile,
                                   @JsonProperty("email") String email, @JsonProperty("address") Address address,
                                   @JsonProperty("customParams") Map<String, String> customParams,
                                   @JsonProperty("firstName") String firstName,
                                   @JsonProperty("middleName") String middleName,
                                   @JsonProperty("lastName") String lastName,
                                   @JsonProperty("cardData") CardData cardData, @JsonProperty("tenure") Integer tenure,
                                   @JsonProperty("cardInfo") CardInfo cardInfo,
                                   @JsonProperty("products") List<PaymentTransactionRequest.ProductInfo> products,
                                   @JsonProperty("pan") String pan, @JsonProperty("dob") String dob,
                                   @JsonProperty("annualIncome") String annualIncome,
                                   @JsonProperty("merchantId") String merchantId,
                                   @JsonProperty("eligibilityResponses") List<EligibilityResponse> eligibilityResponses,
                                   @JsonProperty("merchantSupportedProviders")
                                   List<PaymentProviderEnum> merchantSupportedProviders,
                                   @JsonProperty("source") String source, @JsonProperty("gender") String gender,
                                   @JsonProperty("partner") String partner,
                                   @JsonProperty("providerGroup") String providerGroup) {
        this.consumerName = consumerName;
        this.amount = amount;
        this.mobile = Util.formatMobile(mobile);
        this.email = email;
        this.address = address;
        this.customParams = customParams;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.cardData = cardData;
        this.tenure = tenure;
        this.cardInfo = cardInfo;
        this.products = products;
        this.pan = pan;
        this.dob = dob;
        this.annualIncome = annualIncome;
        this.merchantId = merchantId;
        this.eligibilityResponses = eligibilityResponses;
        this.merchantSupportedProviders = merchantSupportedProviders;
        this.source = source;
        this.gender = gender;
        this.partner = partner;
        this.providerGroup = providerGroup;
    }

}
