package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Mdr {
    private String merchantId;
    private String uuid;
    private String cardType;
    private String bankCode;
    private Integer tenure;
    private String productId;
    private Float rate;
    private Boolean active;
    private Integer score;
    private Float minAmount;
    private Float subvention;
    private Float digitalFinance;
    public Mdr setScore(Integer score) {
        this.score = score;
        return this;
    }

    @JsonCreator
    public Mdr(
            @JsonProperty("merchantId") String merchantId,
            @JsonProperty("uuid") String uuid,
            @JsonProperty("cardType") String cardType,
            @JsonProperty("bankCode") String bankCode,
            @JsonProperty("tenure") Integer tenure,
            @JsonProperty("productId") String productId,
            @JsonProperty("rate") Float rate,
            @JsonProperty("active") Boolean active,
            @JsonProperty("score") Integer score,
            @JsonProperty("minAmount") Float minAmount,
            @JsonProperty("subvention") Float subvention,
            @JsonProperty("digitalFinance") Float digitalFinance) {
        this.merchantId = merchantId;
        this.uuid = uuid;
        this.cardType = cardType;
        this.bankCode = bankCode;
        this.tenure = tenure;
        this.productId = productId;
        this.rate = rate;
        this.active = active;
        this.score = score;
        this.minAmount = minAmount;
        this.subvention = subvention;
        this.digitalFinance = digitalFinance;
    }
}
