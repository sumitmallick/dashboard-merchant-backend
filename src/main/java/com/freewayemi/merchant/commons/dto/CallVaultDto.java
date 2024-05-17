package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallVaultDto {

    private Boolean callVaultForAxisCreditCard;
    private Boolean callVaultForAmexCreditCard;
    private Boolean callVaultForAufbCreditCard;
    private Boolean callVaultForBobCreditCard;
    private Boolean callVaultForCitiCreditCard;
    private Boolean callVaultForHdfcCreditCard;
    private Boolean callVaultForHsbcCreditCard;
    private Boolean callVaultForIciciCreditCard;
    private Boolean callVaultForIndusIndCreditCard;
    private Boolean callVaultForKotakCreditCard;
    private Boolean callVaultForRblCreditCard;
    private Boolean callVaultForSbiCreditCard;
    private Boolean callVaultForScbCreditCard;
    private Boolean callVaultForYesCreditCard;
    private Boolean callVaultForIciciDebitCard;
    private Boolean callVaultForAxisDebitCard;
    private Boolean callVaultForOneCardCreditCard;

    @JsonCreator
    public CallVaultDto(@JsonProperty("callVaultForAxisCreditCard") Boolean callVaultForAxisCreditCard,
                        @JsonProperty("callVaultForAmexCreditCard") Boolean callVaultForAmexCreditCard,
                        @JsonProperty("callVaultForAufbCreditCard") Boolean callVaultForAufbCreditCard,
                        @JsonProperty("callVaultForBobCreditCard") Boolean callVaultForBobCreditCard,
                        @JsonProperty("callVaultForCitiCreditCard") Boolean callVaultForCitiCreditCard,
                        @JsonProperty("callVaultForHdfcCreditCard") Boolean callVaultForHdfcCreditCard,
                        @JsonProperty("callVaultForHsbcCreditCard") Boolean callVaultForHsbcCreditCard,
                        @JsonProperty("callVaultForIciciCreditCard") Boolean callVaultForIciciCreditCard,
                        @JsonProperty("callVaultForIndusIndCreditCard") Boolean callVaultForIndusIndCreditCard,
                        @JsonProperty("callVaultForKotakCreditCard") Boolean callVaultForKotakCreditCard,
                        @JsonProperty("callVaultForRblCreditCard") Boolean callVaultForRblCreditCard,
                        @JsonProperty("callVaultForSbiCreditCard") Boolean callVaultForSbiCreditCard,
                        @JsonProperty("callVaultForScbCreditCard") Boolean callVaultForScbCreditCard,
                        @JsonProperty("callVaultForYesCreditCard") Boolean callVaultForYesCreditCard,
                        @JsonProperty("callVaultForIciciDebitCard") Boolean callVaultForIciciDebitCard,
                        @JsonProperty("callVaultForAxisDebitCard") Boolean callVaultForAxisDebitCard,
                        @JsonProperty("callVaultForOneCardCreditCard") Boolean callVaultForOneCardCreditCard) {
        this.callVaultForAxisCreditCard = callVaultForAxisCreditCard;
        this.callVaultForAmexCreditCard = callVaultForAmexCreditCard;
        this.callVaultForAufbCreditCard = callVaultForAufbCreditCard;
        this.callVaultForBobCreditCard = callVaultForBobCreditCard;
        this.callVaultForCitiCreditCard = callVaultForCitiCreditCard;
        this.callVaultForHdfcCreditCard = callVaultForHdfcCreditCard;
        this.callVaultForHsbcCreditCard = callVaultForHsbcCreditCard;
        this.callVaultForIciciCreditCard = callVaultForIciciCreditCard;
        this.callVaultForIndusIndCreditCard = callVaultForIndusIndCreditCard;
        this.callVaultForKotakCreditCard = callVaultForKotakCreditCard;
        this.callVaultForRblCreditCard = callVaultForRblCreditCard;
        this.callVaultForSbiCreditCard = callVaultForSbiCreditCard;
        this.callVaultForScbCreditCard = callVaultForScbCreditCard;
        this.callVaultForYesCreditCard = callVaultForYesCreditCard;
        this.callVaultForIciciDebitCard = callVaultForIciciDebitCard;
        this.callVaultForAxisDebitCard = callVaultForAxisDebitCard;
        this.callVaultForOneCardCreditCard = callVaultForOneCardCreditCard;
    }
}
