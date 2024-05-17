package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PgConsumerPaymentRequest {
    private final String cardNumber;
    private final String cardToken;
    private final String cvv;
    private final String hdfcLastFourDigits;
    private final Integer tenure;
    private final PartPaymentInfo partPaymentInfo;
    private final String bankCode;
    private final String cardType;
    private final Boolean isSaveCard;
    private final Boolean cxBuyingInsurance;
    private final String cardId;
    private String deviceToken;
    private final String cardSubType;
    private final Integer advanceEmiTenure;
    private final CardData cardData;
    private final String consumerId;
    private final String referralCode;

    @JsonCreator
    public PgConsumerPaymentRequest(@JsonProperty("cardNumber") String cardNumber,
                                    @JsonProperty("cardToken") String cardToken, @JsonProperty("cvv") String cvv,
                                    @JsonProperty("hdfcLastFourDigits") String hdfcLastFourDigits,
                                    @JsonProperty("tenure") Integer tenure,
                                    @JsonProperty("partPaymentInfo") PartPaymentInfo partPaymentInfo,
                                    @JsonProperty("bankCode") String bankCode,
                                    @JsonProperty("cardType") String cardType,
                                    @JsonProperty("isSaveCard") Boolean isSaveCard,
                                    @JsonProperty("cxBuyingInsurance") Boolean cxBuyingInsurance,
                                    @JsonProperty("cardId") String cardId,
                                    @JsonProperty("deviceToken") String deviceToken,
                                    @JsonProperty("cardSubType") String cardSubType,
                                    @JsonProperty("advanceEmiTenure") Integer advanceEmiTenure,
                                    @JsonProperty("cardData") CardData cardData,
                                    @JsonProperty("consumerId") String consumerId,
                                    @JsonProperty("referralCode") String referralCode) {
        this.cardNumber = cardNumber;
        this.cardToken = cardToken;
        this.cvv = cvv;
        this.hdfcLastFourDigits = hdfcLastFourDigits;
        this.tenure = tenure;
        this.partPaymentInfo = partPaymentInfo;
        this.bankCode = bankCode;
        this.cardType = cardType;
        this.isSaveCard = isSaveCard;
        this.cxBuyingInsurance = cxBuyingInsurance;
        this.cardId = cardId;
        this.deviceToken = deviceToken;
        this.cardSubType = cardSubType;
        this.advanceEmiTenure = advanceEmiTenure;
        this.cardData = cardData;
        this.consumerId = consumerId;
        this.referralCode = referralCode;
    }

    @Override
    public String toString() {
        return "cardNumber: " + Util.getMaskCardNumber(cardNumber) + ", cardToken: " + cardToken + ", cvv: " +
                Util.truncateString(cvv) + ", hdfcLastFourDigits: " + hdfcLastFourDigits
                + ", tenure: " + tenure + ", partPaymentInfo: " + partPaymentInfo
                + ", bankCode: " + bankCode
                + ", cardType: " + cardType
                + ", isSaveCard: " + isSaveCard
                + ", cxBuyingInsurance: " + cxBuyingInsurance
                + ", cardId: " + cardId
                + ", cardSubType: " + cardSubType
                + ", cardData: " + cardData
                + ", referralCode: " + referralCode;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
