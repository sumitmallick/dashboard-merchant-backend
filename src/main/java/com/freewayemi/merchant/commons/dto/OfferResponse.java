package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OfferResponse {
    private final String id;
    private final Integer tenure;
    private final Float subvention;
    private final Boolean active;
    private final String cardType;
    private final String productId;
    private final List<String> productIds;
    private final String bankCode;
    private Integer score;
    private String type;
    private Instant validFrom;
    private Instant validTo;
    private final Float minAmount;
    private Float maxOfferAmount;
    private Float offerPercentage;
    private Float bankPercentShare;
    private Float bankShareAmt;
    private Float brandPercentShare;
    private Float brandShareAmt;
    private Float maxBankShare;
    private Float maxBrandShare;
    private Integer velocity;
    private Integer effectiveTenure;
    private Integer offlineAdvanceEmiTenure;
    private Float minMarginDownPaymentAmount;
    private Float maxMarginDownPaymentAmount;
    private List<String> applicableStates;
    private List<String> exclusionStates;
    private final Float maxAmount;

    @JsonCreator
    public OfferResponse(@JsonProperty("id") String id,
                         @JsonProperty("tenure") Integer tenure,
                         @JsonProperty("subvention") Float subvention,
                         @JsonProperty("active") Boolean active,
                         @JsonProperty("cardType") String cardType,
                         @JsonProperty("productId") String productId,
                         @JsonProperty("productIds") List<String> productIds,
                         @JsonProperty("bankCode") String bankCode,
                         @JsonProperty("validFrom") Instant validFrom,
                         @JsonProperty("validTo") Instant validTo,
                         @JsonProperty("minAmount") Float minAmount,
                         @JsonProperty("maxOfferAmount") Float maxOfferAmount,
                         @JsonProperty("offerPercentage") Float offerPercentage,
                         @JsonProperty("bankPercentShare") Float bankPercentShare,
                         @JsonProperty("bankShareAmt") Float bankShareAmt,
                         @JsonProperty("brandPercentShare") Float brandPercentShare,
                         @JsonProperty("brandShareAmt") Float brandShareAmt,
                         @JsonProperty("maxBankShare") Float maxBankShare,
                         @JsonProperty("maxBrandShare") Float maxBrandShare,
                         @JsonProperty("velocity") Integer velocity,
                         @JsonProperty("effectiveTenure") Integer effectiveTenure,
                         @JsonProperty("offlineAdvanceEmiTenure") Integer offlineAdvanceEmiTenure,
                         @JsonProperty("minMarginDownPaymentAmount") Float minMarginDownPaymentAmount,
                         @JsonProperty("maxMarginDownPaymentAmount") Float maxMarginDownPaymentAmount,
                         @JsonProperty("applicableStates") List<String> applicableStates,
                         @JsonProperty("exclusionStates") List<String> exclusionStates,
                         @JsonProperty("maxAmount") Float maxAmount) {
        this.id = id;
        this.tenure = tenure;
        this.subvention = subvention;
        this.active = active;
        this.cardType = cardType;
        this.productId = productId;
        this.productIds = productIds;
        this.bankCode = bankCode;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.minAmount = minAmount;
        this.maxOfferAmount = maxOfferAmount;
        this.offerPercentage = offerPercentage;
        this.bankPercentShare = bankPercentShare;
        this.bankShareAmt = bankShareAmt;
        this.brandPercentShare = brandPercentShare;
        this.brandShareAmt = brandShareAmt;
        this.maxBankShare = maxBankShare;
        this.maxBrandShare = maxBrandShare;
        this.velocity = velocity;
        this.effectiveTenure = effectiveTenure;
        this.offlineAdvanceEmiTenure = offlineAdvanceEmiTenure;
        this.minMarginDownPaymentAmount = minMarginDownPaymentAmount;
        this.maxMarginDownPaymentAmount = maxMarginDownPaymentAmount;
        this.applicableStates = applicableStates;
        this.exclusionStates = exclusionStates;
        this.maxAmount = maxAmount;
    }

    public OfferResponse setScore(Integer score) {
        this.score = score;
        return this;
    }

    public OfferResponse setType(String type) {
        this.type = type;
        return this;
    }
}
