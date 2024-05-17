package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.entity.DownPaymentInfo;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = PaymentRequest.PaymentRequestBuilder.class)
@Builder(builderClassName = "PaymentRequestBuilder", toBuilder = true)
public class PaymentRequest {
    private final String cardId;
    private final String cardToken;
    private final Boolean isVaultToken;
    private final Boolean isEmi;
    private final String bank;
    private final Integer tenure;
    private final Float pgAmount;
    private final String consumerEmail;
    private final Float discount;
    private final Float emi;
    private final Float irr;
    private final Float bankCharges;
    private final String bankName;
    private final String cardType;
    private final DownPaymentInfo downPaymentInfo;
    private final String cardBrand;
    private final Boolean getCxBuyingInsurance;
    private final String cardSubType;
    private final Integer advanceEmiTenure;
    private final String cardNumber;
    private final String expMonth;
    private final String expYear;
    private final String cvv;
    private final String referralCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PaymentRequestBuilder {
    }

    @Override
    public String toString() {
        return "cardId: " + cardId + ", cardToken: " + cardToken + ", isVaultToken: " + isVaultToken + ", isEmi: " +
                isEmi + ", bank: " + bank + ", tenure: " + tenure + ", pgAmount: " + pgAmount + ", consumerEmail: " +
                consumerEmail + ", discount: " + discount + ", emi: " + emi + ", irr: " + irr + ", bankCharges: " +
                bankCharges + ", bankName: " + bankName + ", cardType: " + cardType + ", downPaymentInfo: " +
                downPaymentInfo + ", cardBrand: " + cardBrand + ", cardSubType: " + cardSubType + ", cardNumber: " +
                Util.getMaskCardNumber(cardNumber) + ", expMonth: " + expMonth + ", expYear: " + expYear + ", cvv: " +
                Util.truncateString(cvv)+ ", referralCode: " + referralCode;
    }
}
