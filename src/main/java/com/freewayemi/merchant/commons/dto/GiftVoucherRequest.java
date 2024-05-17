package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GiftVoucherRequest {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String pincode;
    private final Integer amount;
    private final String sku;
    private final String brandImage;
    private final String consumerId;
    private final String transactionId;
    private final String bankName;
    private final String cardType;
    private final Float emi;
    private final Integer tenure;
    private final String product;
    private final String productId;

    @JsonCreator
    public GiftVoucherRequest(@JsonProperty("email") String email,
                              @JsonProperty("firstName") String firstName,
                              @JsonProperty("lastName") String lastName,
                              @JsonProperty("pincode") String pincode,
                              @JsonProperty("amount") Integer amount,
                              @JsonProperty("sku") String sku,
                              @JsonProperty("brandImage") String brandImage,
                              @JsonProperty("consumerId") String consumerId,
                              @JsonProperty("transactionId") String transactionId,
                              @JsonProperty("bankName") String bankName,
                              @JsonProperty("cardType") String cardType,
                              @JsonProperty("emi") Float emi,
                              @JsonProperty("tenure") Integer tenure,
                              @JsonProperty("product") String product,
                              @JsonProperty("productId") String productId) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pincode = pincode;
        this.amount = amount;
        this.sku = sku;
        this.brandImage = brandImage;
        this.consumerId = consumerId;
        this.transactionId = transactionId;
        this.bankName = bankName;
        this.cardType = cardType;
        this.emi = emi;
        this.tenure = tenure;
        this.product = product;
        this.productId = productId;
    }
}
