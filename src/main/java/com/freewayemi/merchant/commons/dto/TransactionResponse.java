package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.downpayment.DownPaymentConfigDto;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.dto.refund.RefundResponse;
import com.freewayemi.merchant.commons.entity.DownPaymentInfo;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {
    private final String txnId;
    private final String qrCode;
    private final String mobile;
    private final String email;
    private final String status;
    private final Integer statusCode;
    private final String statusMessage;
    private final String displayDate;
    private final String txnSuccessDate;
    private final Float amount;
    private final Float splitAmount;
    private final Float pgAmount;
    private final Float discount;
    private final Float emi;
    private final Float irr;
    private final Integer tenure;
    private final Float bankCharges;
    private final String merchantName;
    private final String merchantDisplayId;
    private final String merchantOrderId;
    private final Float merchantDiscount;
    private final Float settlementAmount;
    private final Float gstCharges;
    private final Float mdrCharges;
    private final Float convFeeCharges;
    private final Float gstConvFeeCharges;
    private final Float netSettlementAmount;
    private final Float netConsumerAmount;
    private final Float invoiceAmount;
    private final String settlementStatus;
    private final String expectedSettlementDate;
    private final List<PriceResponse> offers;
    private final String bankName;
    private final String cardType;
    private final String productId;
    private final String productName;
    private final List<OfferResponse> pgOffers;
    private final Boolean canCxBuyInsurance;
    private final Boolean isConvFee;
    private final List<ConvFeeRate> convFeeRates;
    private final Params merchantParams;
    private final RefundResponse refund;
    private final String consumerId;
    private final String merchantId;
    private final String eligibility;
    private final List<String> eligibilities;
    private final PaymentProviderEnum paymentProvider;
    private final Boolean downPaymentEnabled;
    private final String paymentLinkId;
    private final String merchantReturnUrl;
    private final String consumerName;
    private final Address address;
    private final Map<String, String> customParams;
    private final Boolean iframe;
    private final String merchantType;
    private final String source;
    private Map<String, List<PriceResponse>> cardOffers;
    private DownPaymentInfo downPaymentInfo;
    private final Integer maxTenure;
    private final Boolean isSeamless;
    private final AdditionInfo additionInfo;
    private final String storeLinkId;
    private final Boolean isSecureApiMerchant;
    private final Boolean isBrandSubventionModel;
    private final List<OfferResponse> brandSubventions;
    private final Float cashbackCharges;
    private final Boolean cxBuyingInsurance;
    private final String merchantWebhookUrl;
    private final String storeUserId;
    private final String storeUserName;
    private final String storeUserMobile;
    private final List<EligibilityResponse> ntbEligibilities;
    private final Float processingFee;
    private final Float gstOnProcessingFee;
    private final Integer advanceEmiTenure;

    private String transactionRating;
    private Instant paymentLinkExpiryDate;
    private final String consumerGender;
    private final String consumerDob;
    private final CallVaultDto callVault;
    private final String bankCode;
    private final Boolean isSeamlessPaymentLink;

    private final Boolean isSubvented;

    private final String merchantProvidedSubvention;
    private final DownPaymentConfigDto downPaymentConfig;

    private String paymentUrl;
    private final String bankReferenceNo;
    private final List<PaymentTransactionRequest.ProductInfo> productInfo;
    private final BankInterestDto bankInterestDtoOnMerchant;
    private final BankInterestDto bankInterestDtoOnBrand;

    private final Float orderAmount;
    private final Integer offlineAdvanceEmiTenure;

    private Boolean isInstantCashbackEnabled;
    private MerchantInstantDiscountConfigResp merchantInstantDiscountConfigResp;
    private List<PaymentProviderInfo> providers;

    private final Float additionalDiscountAmount;
    private final String additionalDiscountAmountType;
    private final Float additionalCashback;
    private final String additionalCashbackType;
    private final Float additionalInstantDiscount;
    private final String partner;
    private final Double dbd;
    private final Double dbdAmount;
    private final Double mbd;
    private final Double mbdAmount;
    private final String serialNumber;
    private final Float additionalCashbackAmount;
    private final Float totalCashback;
    private final String masterMerchants;
    private final String encryptedTxnId;

    @JsonCreator
    public TransactionResponse(@JsonProperty("txnId") String txnId, @JsonProperty("qrCode") String qrCode,
                               @JsonProperty("mobile") String mobile, @JsonProperty("email") String email,
                               @JsonProperty("status") String status, @JsonProperty("statusCode") Integer statusCode,
                               @JsonProperty("statusMessage") String statusMessage,
                               @JsonProperty("displayDate") String displayDate,
                               @JsonProperty("txnSuccessDate") String txnSuccessDate,
                               @JsonProperty("amount") Float amount, @JsonProperty("splitAmount") Float splitAmount,
                               @JsonProperty("pgAmount") Float pgAmount, @JsonProperty("discount") Float discount,
                               @JsonProperty("emi") Float emi, @JsonProperty("irr") Float irr,
                               @JsonProperty("tenure") Integer tenure, @JsonProperty("bankCharges") Float bankCharges,
                               @JsonProperty("merchantName") String merchantName,
                               @JsonProperty("merchantDisplayId") String merchantDisplayId,
                               @JsonProperty("merchantOrderId") String merchantOrderId,
                               @JsonProperty("merchantDiscount") Float merchantDiscount,
                               @JsonProperty("settlementAmount") Float settlementAmount,
                               @JsonProperty("gstCharges") Float gstCharges,
                               @JsonProperty("mdrCharges") Float mdrCharges,
                               @JsonProperty("convFeeCharges") Float convFeeCharges,
                               @JsonProperty("gstConvFeeCharges") Float gstConvFeeCharges,
                               @JsonProperty("netSettlementAmount") Float netSettlementAmount,
                               @JsonProperty("netConsumerAmount") Float netConsumerAmount,
                               @JsonProperty("invoiceAmount") Float invoiceAmount,
                               @JsonProperty("settlementStatus") String settlementStatus,
                               @JsonProperty("expectedSettlementDate") String expectedSettlementDate,
                               @JsonProperty("offers") List<PriceResponse> offers,
                               @JsonProperty("bankName") String bankName, @JsonProperty("cardType") String cardType,
                               @JsonProperty("productId") String productId,
                               @JsonProperty("productName") String productName,
                               @JsonProperty("pgOffers") List<OfferResponse> pgOffers,
                               @JsonProperty("canCxBuyInsurance") Boolean canCxBuyInsurance,
                               @JsonProperty("isConvFee") Boolean isConvFee,
                               @JsonProperty("convFeeRates") List<ConvFeeRate> convFeeRates,
                               @JsonProperty("merchantParams") Params merchantParams,
                               @JsonProperty("refund") RefundResponse refund,
                               @JsonProperty("merchantId") String merchantId,
                               @JsonProperty("consumerId") String consumerId,
                               @JsonProperty("eligibility") String eligibility,
                               @JsonProperty("eligibilities") List<String> eligibilities,
                               @JsonProperty("paymentProvider") PaymentProviderEnum paymentProvider,
                               @JsonProperty("downPaymentEnabled") Boolean downPaymentEnabled,
                               @JsonProperty("downPaymentInfo") DownPaymentInfo downPaymentInfo,
                               @JsonProperty("paymentLinkId") String paymentLinkId,
                               @JsonProperty("merchantReturnUrl") String merchantReturnUrl,
                               @JsonProperty("consumerName") String consumerName,
                               @JsonProperty("address") Address address,
                               @JsonProperty("customParams") Map<String, String> customParams,
                               @JsonProperty("iframe") Boolean iframe,
                               @JsonProperty("merchantType") String merchantType, @JsonProperty("source") String source,
                               @JsonProperty("maxTenure") Integer maxTenure,
                               @JsonProperty("isSeamless") Boolean isSeamless,
                               @JsonProperty("additionInfo") AdditionInfo additionInfo,
                               @JsonProperty("storeLinkId") String storeLinkId,
                               @JsonProperty("isSecureApiMerchant") Boolean isSecureApiMerchant,
                               @JsonProperty("isBrandSubventionModel") Boolean isBrandSubventionModel,
                               @JsonProperty("brandSubventions") List<OfferResponse> brandSubventions,
                               @JsonProperty("cashbackCharges") Float cashbackCharges,
                               @JsonProperty("cxBuyingInsurance") Boolean cxBuyingInsurance,
                               @JsonProperty("merchantWebhookUrl") String merchantWebhookUrl,
                               @JsonProperty("storeUserId") String storeUserId,
                               @JsonProperty("storeUserName") String storeUserName,
                               @JsonProperty("storeUserMobile") String storeUserMobile,
                               @JsonProperty("ntbEligibilities") List<EligibilityResponse> ntbEligibilities,
                               @JsonProperty("processingFee") Float processingFee,
                               @JsonProperty("gstOnProcessingFee") Float gstOnProcessingFee,
                               @JsonProperty("advanceEmiTenure") Integer advanceEmiTenure,
                               @JsonProperty("consumerGender") String consumerGender,
                               @JsonProperty("consumerDob") String consumerDob,
                               @JsonProperty("callVault") CallVaultDto callVault,
                               @JsonProperty("bankCode") String bankCode,
                               @JsonProperty("isSeamlessPaymentLink") Boolean isSeamlessPaymentLink,
                               @JsonProperty("isSubvented") Boolean isSubvented,
                               @JsonProperty("merchantProvidedSubvention") String merchantProvidedSubvention,
                               @JsonProperty("downPaymentConfig") DownPaymentConfigDto downPaymentConfig,
                               @JsonProperty("bankReferenceNo") String bankReferenceNo,
                               @JsonProperty("productInfo") List<PaymentTransactionRequest.ProductInfo> productInfo,
                               @JsonProperty("bankInterestDtoOnMerchant") BankInterestDto bankInterestDtoOnMerchant,
                               @JsonProperty("bankInterestDtoOnBrand") BankInterestDto bankInterestDtoOnBrand,
                               @JsonProperty("orderAmount") Float orderAmount,
                               @JsonProperty("offlineAdvanceEmiTenure") Integer offlineAdvanceEmiTenure,
                               @JsonProperty("additionalCashback")  Float additionalCashback,
                               @JsonProperty("additionalCashbackType") String additionalCashbackType,
                               @JsonProperty("additionalDiscountAmount") Float additionalDiscountAmount,
                               @JsonProperty("additionalDiscountAmountType") String additionalDiscountAmountType,
                               @JsonProperty("additionalInstantDiscount") Float additionalInstantDiscount,
                               @JsonProperty("partner") String partner,
                               @JsonProperty("dbd") Double dbd,
                               @JsonProperty("dbdAmount") Double dbdAmount,
                               @JsonProperty("mbd") Double mbd,
                               @JsonProperty("mbdAmount") Double mbdAmount,
                               @JsonProperty("serialNumber") String serialNumber,
                               @JsonProperty("additionalCashbackAmount") Float additionalCashbackAmount,
                               @JsonProperty("totalCashback") Float totalCashback,
                               @JsonProperty("masterMerchants") String masterMerchants,
                               @JsonProperty("encryptedTxnId") String encryptedTxnId) {

        this.txnId = txnId;
        this.qrCode = qrCode;
        this.mobile = mobile;
        this.email = email;
        this.status = status;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.displayDate = displayDate;
        this.txnSuccessDate = txnSuccessDate;
        this.amount = Util.getFLoat(amount);
        this.splitAmount = Util.getFLoat(splitAmount);
        this.merchantName = merchantName;
        this.merchantDisplayId = merchantDisplayId;
        this.merchantOrderId = merchantOrderId;
        this.offers = offers;
        this.pgAmount = Util.getFLoat(pgAmount);
        this.discount = Util.getFLoat(discount);
        this.emi = Util.getFLoat(emi);
        this.irr = Util.getFLoat(irr);
        this.tenure = tenure;
        this.bankCharges = Util.getFLoat(bankCharges);
        this.merchantDiscount = Util.getFLoat(merchantDiscount);
        this.settlementAmount = Util.getFLoat(settlementAmount);
        this.netConsumerAmount = Util.getFLoat(netConsumerAmount);
        this.invoiceAmount = Util.getFLoat(invoiceAmount);
        this.settlementStatus = settlementStatus;
        this.expectedSettlementDate = expectedSettlementDate;
        this.bankName = bankName;
        this.cardType = cardType;
        this.gstCharges = Util.getFLoat(gstCharges);
        this.mdrCharges = Util.getFLoat(mdrCharges);
        this.convFeeCharges = Util.getFLoat(convFeeCharges);
        this.gstConvFeeCharges = Util.getFLoat(gstConvFeeCharges);
        this.netSettlementAmount = Util.getFLoat(netSettlementAmount);
        this.productId = productId;
        this.productName = productName;
        this.pgOffers = pgOffers;
        this.isConvFee = null != isConvFee && isConvFee;
        this.canCxBuyInsurance = null != canCxBuyInsurance && canCxBuyInsurance;
        this.convFeeRates = convFeeRates;
        this.merchantParams = merchantParams;
        this.refund = refund;
        this.consumerId = consumerId;
        this.merchantId = merchantId;
        this.eligibility = eligibility;
        this.eligibilities = eligibilities;
        this.paymentProvider = paymentProvider;
        this.downPaymentEnabled = downPaymentEnabled;
        this.downPaymentInfo = downPaymentInfo;
        this.paymentLinkId = paymentLinkId;
        this.merchantReturnUrl = merchantReturnUrl;
        this.consumerName = consumerName;
        this.address = address;
        this.customParams = customParams;
        this.iframe = iframe;
        this.merchantType = merchantType;
        this.source = source;
        this.maxTenure = maxTenure;
        this.isSeamless = isSeamless;
        this.additionInfo = additionInfo;
        this.storeLinkId = storeLinkId;
        this.isSecureApiMerchant = isSecureApiMerchant;
        this.isBrandSubventionModel = isBrandSubventionModel;
        this.brandSubventions = brandSubventions;
        this.cashbackCharges = cashbackCharges;
        this.cxBuyingInsurance = cxBuyingInsurance;
        this.merchantWebhookUrl = merchantWebhookUrl;
        this.storeUserId = storeUserId;
        this.storeUserName = storeUserName;
        this.storeUserMobile = storeUserMobile;
        this.ntbEligibilities = ntbEligibilities;
        this.processingFee = processingFee;
        this.gstOnProcessingFee = gstOnProcessingFee;
        this.advanceEmiTenure = advanceEmiTenure;
        this.consumerGender = consumerGender;
        this.consumerDob = consumerDob;
        this.callVault = callVault;
        this.bankCode = bankCode;
        this.isSeamlessPaymentLink = isSeamlessPaymentLink;
        this.isSubvented = isSubvented;
        this.merchantProvidedSubvention = merchantProvidedSubvention;
        this.downPaymentConfig = downPaymentConfig;
        this.bankReferenceNo = bankReferenceNo;
        this.productInfo = productInfo;
        this.bankInterestDtoOnMerchant = bankInterestDtoOnMerchant;
        this.bankInterestDtoOnBrand = bankInterestDtoOnBrand;
        this.orderAmount = orderAmount;
        this.offlineAdvanceEmiTenure = offlineAdvanceEmiTenure;
        this.additionalDiscountAmount = additionalDiscountAmount;
        this.additionalDiscountAmountType = additionalDiscountAmountType;
        this.additionalCashback = additionalCashback;
        this.additionalCashbackType = additionalCashbackType;
        this.additionalInstantDiscount = additionalInstantDiscount;
        this.partner = partner;
        this.dbd = dbd;
        this.dbdAmount = dbdAmount;
        this.mbd = mbd;
        this.mbdAmount = mbdAmount;
        this.serialNumber = serialNumber;
        this.additionalCashbackAmount = additionalCashbackAmount;
        this.totalCashback = totalCashback;
        this.masterMerchants = masterMerchants;
        this.encryptedTxnId = encryptedTxnId;
    }

    @JsonProperty("cardOffers")
    public Map<String, List<PriceResponse>> getCardOffers() {
        return cardOffers;
    }

    public void setCardOffers(Map<String, List<PriceResponse>> cardOffers) {
        this.cardOffers = cardOffers;
    }

}
