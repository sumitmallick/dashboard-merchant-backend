package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.DisplayEligibilityBand;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class Params {
    private List<Float> validAmounts;

    private String leadOwnerId;
    private String leadId;
    private String brandId;

    @JsonProperty("exclusion_credit_banks")
    @Field("exclusion_credit_banks")
    private String exclusionCreditBanks;

    private List<String> leadOwnerIds;
    private List<String> brandIds;

    @JsonProperty("Top100")
    @Field("Top100")
    private Boolean top100;
    private String logo;
    private String sales;
    private String sideBannerUrl;
    private String plBannerUrl;
    private String settlementMailIds;
    private String accowner;
    private String brand;

    @JsonProperty("exclusion_payment_types")
    @Field("exclusion_payment_types")
    private String exclusionPaymentTypes;

    @JsonProperty("exclusion_debit_banks")
    @Field("exclusion_debit_banks")
    private String exclusionDebitBanks;

    @JsonProperty("billdesk_merchantId")
    @Field("billdesk_merchantId")
    private String billDeskMerchantId;
    private String skipInfoForm;
    private String skipNoCostEmiText;

    @JsonProperty("subvent_gst")
    @Field("subvent_gst")
    private String subventGst;
    private String federalnbfc;
    private String skipIntro;
    private String skipRating;
    private String delayedUserInfoForm;
    private String hdfcDCStoreId;
    private String hdfcDCStoreName;
    private String logoUrl;
    private String externalStoreCode;
    private String showEligibilityTab;
    private String hideCreatePaymentLink;
    private String custompaymentTxnIdWithLength;

    @JsonProperty("exclusion_tenures")
    @Field("exclusion_tenures")
    private String exclusionTenures;
    private Boolean isMaf;
    private String mafId;
    private String sku;
    private String skuBrandImage;
    private String tradeDiscount;
    private String showIciciCardless18Months;
    private String showHdfcDcEmi24Months;
    private String promoCode;
    private Instant promoCodeExpiry;
    private Float promoCodeSubPer;
    private Float promoCodeMaxAmount;
    private String approvedBy;
    private Map<String, Integer> productMaxTenureMapping;
    private String showHdfcCardless;
    private String sendTxnAlerts;
    private Boolean fetchConsumerCreditScore;
    private String showKotakCardless;
    private Boolean showFirstName;
    private Boolean showLastName;
    private Boolean showPincode;
    private Boolean showGender;
    private Boolean showDOB;
    private String salesAppVersion;
    private Boolean isPromoCodeEnableForNoCostEMI;
    private Boolean isWebhookResponseAsJson;
    private Boolean isAllowRefundPayout;
    private Boolean doNotAutoPopulatePLProductName;
    private String storeClassification;
    private Boolean subventProcessingFee;
    private Boolean provideEligibilityByBank;
    private Boolean isGetDeliveryOrderDetailsAllowed;
    private Boolean serialNoValidationRequired;
    private Boolean asyncClaim;
    private Boolean callVaultForIciciDebitCard;
    private Boolean callVaultForAxisDebitCard;
    private Boolean useTokenizationForAxisDebitCard;
    private Boolean useTokenizationForIciciDebitCard;
    private Boolean callVaultForAxisCreditCard;
    private Boolean callVaultForAufbCreditCard;
    private Boolean callVaultForAmexCreditCard;
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
    private String checkoutVersion;
    private List<Float> merchantProfiledCoordinates;
    private Boolean processRefundsOnProRetaBasis;
    private Boolean isFixDownpaymentFlowEnabled;
    private Boolean isPreCheckoutOfferEnabled;
    private Boolean callVaultForOneCardCreditCard;
    private Boolean enableDownpaymentByDebitCard;
    private Boolean otpVerificationRequired;
    private Boolean skipLoanBooking;
    private List<String> daCopyEmailIdList;
    private String merchandiseNotes;
    private Boolean isEligibilitiesWithBankDetails;
    private Boolean isAutoRedirectToMerchantUrl;
    private Boolean isOnlineInquiryEnabled;
    private Boolean hideRetryTxnOption;
    private Boolean isOldCheckoutDesign;
    private Boolean disableOthersOption;
    private Boolean isIciciNtbFlowEnabled;
    private Boolean enableDpBySchemeConfig;
    private Boolean disableDpReCalculation;
    private Boolean disableNTBEligibility;
    private Boolean productSkuRequired;
    private Boolean enableDefaultBrandProduct;
    private DisplayEligibilityBand displayEligibilityBand;
    private Boolean encTxnLinkEnabled;
    private Boolean splitEligibilities;
    private Boolean maskPIData;
    private Boolean isConvFeeFromConfiguredRatesOnly;
    private Boolean dashboardNTBEligibilityCheck;
    private String asyncClaimTATInDays;
    private Boolean retryMechanismEnabled;
}
