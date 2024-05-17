package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AdditionInfo {

    private final String cardLastFourDigit;
    private final String availableLimit;
    private final String giftVoucherId;
    private final String txnInvoiceId;
    private final List<BanksMaxEligibilityTenure> banksMaxEligibilityTenure;
    private final String cardId;
    private final String cashbackStatus;
    private final String payoutStatus;
    private final Instant expectedCashbackDate;
    private final String cxBuyingInsuranceReportUrl;
    private final Integer dcMaxTenure;
    private final Integer ccMaxTenure;
    private final BrandInfo brandInfo;
    private final String payLinkCreateBy;
    private final String loanId;
    private final String loanStatus;
    private final String loanSubStatus;
    private final Float promoCodeAmount;
    private final Boolean promoCodeSuccess;
    private final Instant promoCodeExpectedCashbackDate;
    private final String mccCode;
    private final String redirectStage;

    @JsonCreator
    public AdditionInfo(@JsonProperty("cardLastFourDigit") String cardLastFourDigit,
                        @JsonProperty("availableLimit") String availableLimit,
                        @JsonProperty("giftVoucherId") String giftVoucherId,
                        @JsonProperty("txnInvoiceId") String txnInvoiceId,
                        @JsonProperty("banksMaxEligibilityTenure")
                                List<BanksMaxEligibilityTenure> banksMaxEligibilityTenure,
                        @JsonProperty("cardId") String cardId,
                        @JsonProperty("cashbackStatus") String cashbackStatus,
                        @JsonProperty("payoutStatus") String payoutStatus,
                        @JsonProperty("expectedCashbackDate") Instant expectedCashbackDate,
                        @JsonProperty("cxBuyingInsuranceReportUrl") String cxBuyingInsuranceReportUrl,
                        @JsonProperty("dcMaxTenure") Integer dcMaxTenure,
                        @JsonProperty("ccMaxTenure") Integer ccMaxTenure,
                        @JsonProperty("brandInfo") BrandInfo brandInfo,
                        @JsonProperty("payLinkCreateBy") String payLinkCreateBy,
                        @JsonProperty("loanId") String loanId,
                        @JsonProperty("loanStatus") String loanStatus,
                        @JsonProperty("loanSubStatus") String loanSubStatus,
                        @JsonProperty("promoCodeAmount") Float promoCodeAmount,
                        @JsonProperty("promoCodeSuccess") Boolean promoCodeSuccess,
                        @JsonProperty("promoCodeExpectedCashbackDate") Instant promoCodeExpectedCashbackDate,
                        @JsonProperty("mccCode") String mccCode,
                        @JsonProperty("redirectStage") String redirectStage) {
        this.cardLastFourDigit = cardLastFourDigit;
        this.availableLimit = availableLimit;
        this.giftVoucherId = giftVoucherId;
        this.txnInvoiceId = txnInvoiceId;
        this.banksMaxEligibilityTenure = banksMaxEligibilityTenure;
        this.cardId = cardId;
        this.cashbackStatus = cashbackStatus;
        this.payoutStatus = payoutStatus;
        this.expectedCashbackDate = expectedCashbackDate;
        this.cxBuyingInsuranceReportUrl = cxBuyingInsuranceReportUrl;
        this.dcMaxTenure = dcMaxTenure;
        this.ccMaxTenure = ccMaxTenure;
        this.brandInfo = brandInfo;
        this.payLinkCreateBy = payLinkCreateBy;
        this.loanId = loanId;
        this.loanStatus = loanStatus;
        this.loanSubStatus = loanSubStatus;
        this.promoCodeAmount = promoCodeAmount;
        this.promoCodeSuccess = promoCodeSuccess;
        this.promoCodeExpectedCashbackDate = promoCodeExpectedCashbackDate;
        this.mccCode = mccCode;
        this.redirectStage = redirectStage;
    }

}
