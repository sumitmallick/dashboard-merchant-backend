package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class OfferRequest {
    private final String type;
    private final String merchantId;
    private final String category;
    @NotNull(message = "Please provide tenure.")
    private final Integer tenure;
    @NotNull(message = "Please provide subvention.")
    private final float subvention;

    private final String cardType;
    private final String product;
    private final String bankCode;

    private final Instant validFrom;
    private final Instant validTo;

    @JsonCreator
    public OfferRequest(@JsonProperty("type") String type,
                        @JsonProperty("merchantId") String merchantId,
                        @JsonProperty("category") String category,
                        @JsonProperty("tenure") Integer tenure,
                        @JsonProperty("subvention") float subvention,
                        @JsonProperty("cardType") String cardType,
                        @JsonProperty("product") String product,
                        @JsonProperty("bankCode") String bankCode,
                        @JsonProperty("validFrom") Instant validFrom,
                        @JsonProperty("validTo") Instant validTo) {
        this.type = type;
        this.merchantId = merchantId;
        this.category = category;
        this.tenure = tenure;
        this.subvention = subvention;
        this.cardType = cardType;
        this.product = product;
        this.bankCode = bankCode;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
}
