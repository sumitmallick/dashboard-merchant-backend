package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.offer.DynamicOfferResponse;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString
public class TransactionRequest {
    @NotNull(message = "Please provide amount.")
    private final Float amount;
    @NotNull(message = "Please provide mobile.")
    private final String mobile;
    @NotNull(message = "Please provide offers.")
    private final List<String> offers;
    private final String productName;
    private final DynamicOfferResponse dynamicOffersConfig;
    private final String invoiceNumber;
    private final String quantity;
    private final String catalogProductId;
    private final BrandRequest brandRequest;
    private final Integer tenure;
    private final CardInfo cardInfo;

    @JsonCreator
    public TransactionRequest(@JsonProperty("amount") Float amount,
                              @JsonProperty("mobile") String mobile,
                              @JsonProperty("offers") List<String> offers,
                              @JsonProperty("productName") String productName,
                              @JsonProperty("dynamicOffersConfig") DynamicOfferResponse dynamicOffersConfig,
                              @JsonProperty("invoiceNumber") String invoiceNumber,
                              @JsonProperty("quantity") String quantity,
                              @JsonProperty("catalogProductId") String catalogProductId,
                              @JsonProperty("brandRequest") BrandRequest brandRequest,
                              @JsonProperty("tenure") Integer tenure,
                              @JsonProperty("cardInfo") CardInfo cardInfo
    ) {
        this.amount = amount;
        this.mobile = mobile;
        this.offers = offers;
        this.productName = productName;
        this.dynamicOffersConfig = dynamicOffersConfig;
        this.invoiceNumber = invoiceNumber;
        this.quantity = quantity;
        this.catalogProductId = catalogProductId;
        this.brandRequest = brandRequest;
        this.tenure = tenure;
        this.cardInfo = cardInfo;
    }
}
