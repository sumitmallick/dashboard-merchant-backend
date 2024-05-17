package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.BankEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ApplicableOfferRequest {
    @NotNull(message = "Please provide brand id.")
    String brandId;
    @NotNull(message = "Please provide transaction Id.")
    String transactionId;
    @NotNull(message = "Please provide interest.")
    Float irrpa;
    @NotNull(message = "Please provide effective card type.")
    String effectiveCardType;
    @NotNull(message = "Please provide bank enum.")
    BankEnum bankEnum;
    @NotNull(message = "Please provide emi tenure.")
    Integer emiTenure;

    @JsonCreator
    public ApplicableOfferRequest(@JsonProperty("brandId") String brandId,
                                  @JsonProperty("transactionId") String transactionId,
                                  @JsonProperty("irrpa") Float irrpa,
                                  @JsonProperty("effectiveCardType") String effectiveCardType,
                                  @JsonProperty("bankEnum") BankEnum bankEnum,
                                  @JsonProperty("emiTenure") Integer emiTenure){
        this.brandId = brandId;
        this.transactionId = transactionId;
        this.irrpa = irrpa;
        this.effectiveCardType = effectiveCardType;
        this.bankEnum = bankEnum;
        this.emiTenure = emiTenure;
    }
}
