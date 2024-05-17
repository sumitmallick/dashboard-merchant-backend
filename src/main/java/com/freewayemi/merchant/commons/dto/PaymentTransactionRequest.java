package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.dto.offer.DynamicOffer;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class PaymentTransactionRequest extends TransactionRequest {
    private final MerchantInfo merchantInfo;
    private final ConsumerInfo consumerInfo;
    private final ProductInfo product;
    private final String status;
    private final String source;
    private final Boolean isRetryTransaction;
    private final String parentTransactionId;
    private final CardData cardData;
    private final Integer tenure;
    private final CardInfo cardInfo;
    private final LoanInfo loanInfo;
    private final String latitude;
    private final String longitude;
    private final String ip;
    private final Boolean isSeamlessPaymentLink;
    private final Boolean isSubvention;
    private final String subventionAmount;
    private final String partner;

    public PaymentTransactionRequest(@JsonProperty("amount") Float amount, @JsonProperty("mobile") String mobile,
                                     @JsonProperty("offers") List<String> offers,
                                     @JsonProperty("merchantInfo") MerchantInfo merchantInfo,
                                     @JsonProperty("consumerInfo") ConsumerInfo consumerInfo,
                                     @JsonProperty("status") String status,
                                     @JsonProperty("product") ProductInfo product,
                                     @JsonProperty("source") String source,
                                     @JsonProperty("isRetryTransaction") Boolean isRetryTransaction,
                                     @JsonProperty("parentTransactionId") String parentTransactionId,
                                     @JsonProperty("cardData") CardData cardData,
                                     @JsonProperty("tenure") Integer tenure,
                                     @JsonProperty("cardInfo") CardInfo cardInfo, @JsonProperty("loanInfo") LoanInfo loanInfo,
                                     @JsonProperty("latitude") String latitude, @JsonProperty("longitude") String longitude,
                                     @JsonProperty("ip") String ip,
                                     @JsonProperty("isSeamlessPaymentLink") Boolean isSeamlessPaymentLink,
                                     @JsonProperty("isSubvention") Boolean isSubvention,
                                     @JsonProperty("subventionAmount") String subventionAmount,
                                     @JsonProperty("partner") String partner) {
        super(amount, mobile, offers, "", null, null,
                null, null, null, null, null);
        this.status = status;
        this.product = product;
        this.merchantInfo = merchantInfo;
        this.consumerInfo = consumerInfo;
        this.source = source;
        this.isRetryTransaction = isRetryTransaction;
        this.parentTransactionId = parentTransactionId;
        this.cardData = cardData;
        this.tenure = tenure;
        this.cardInfo = cardInfo;
        this.loanInfo = loanInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.isSeamlessPaymentLink = isSeamlessPaymentLink;
        this.isSubvention = isSubvention;
        this.subventionAmount = subventionAmount;
        this.partner = partner;
    }

    @Data
    @JsonDeserialize(builder = MerchantInfo.MerchantInfoBuilder.class)
    @Builder(builderClassName = "MerchantInfoBuilder", toBuilder = true)
    public static class MerchantInfo {
        private final String merchantId;
        private final String merchantDisplayId;
        private final String merchantName;
        private final String merchantMobile;
        private final String merchantEmail;
        private final String merchantType;
        private final String merchantOrderId;
        private final String merchantReturnUrl;
        private final String merchantWebhookUrl;
        private final String merchantDeviceToken;
        private final Params merchantParams;
        private final List<OfferResponse> offerResponses;
        private final List<Mdr> mdrs;
        private final List<ConvFeeRate> convFeeRates;
        private final List<PaymentProviderEnum> supportedProviders;
        private final Boolean downPaymentEnabled;
        private final List<PaymentProviderInfo> supportedDpProviders;
        private final List<PaymentProviderInfo> allowedProviders;
        private final String paymentLinkId;
        private final String payLinkCreatedBy;
        private final Map<String, String> customParams;
        private Integer maxTenure;
        private Integer dcMaxTenure;
        private Integer ccMaxTenure;
        private final String category;
        private final String subCategory;
        private final String mccCode;
        private final Boolean isSeamless;
        private final Boolean isDynamicOffer;
        private final Boolean canCxBuyInsurance;
        private final Boolean isConvFee;
        private final List<DynamicOffer> dynamicOffers;
        private final String storeLinkId;
        private final Boolean isInvoiceEnabled;
        private final Boolean isGiftVoucherEnabled;
        private final Boolean isInvoicingModel;
        private final Boolean isBrandSubventionModel;
        private final List<OfferResponse> brandSubventions;
        private final BrandInfo brandInfo;
        private final String storeUserId;
        private final String storeUserMobile;
        private final String storeUserName;
        private final String gst;
        private final List<MerchantDiscountRateResponse> brandMdrs;
        private final Address address;
//        private final BrandPaymentConfigDto brandPaymentConfigDto;
        private final SettlementConfigDto settlementConfigDto;
        private final BankInterestDto bankInterestDtoOnMerchant;
        private final BankInterestDto bankInterestDtoOnBrand;
        private final String partner;
        private final String masterMerchants;
        private final String businessName;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MerchantInfoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = ConsumerInfo.ConsumerInfoBuilder.class)
    @Builder(builderClassName = "ConsumerInfoBuilder", toBuilder = true)
    public static class ConsumerInfo {
        private final String consumerId;
        private final String email;
        private final String mobile;
        private final String preferredEligibility;
        private final Float splitAmount;
        private final String consumerName;
        private final Address address;
        private final Boolean iframe;
        private final String firstName;
        private final String middleName;
        private final String lastName;
        private final String returnUrl;
        private final Boolean isMobileChange;
        private final String pan;
        private final String dob;
        private final String annualIncome;
        private final String referredBy;
        private final String gender;
        private final String appVersion;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ConsumerInfoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = ProductInfo.ProductInfoBuilder.class)
    @Builder(builderClassName = "ProductInfoBuilder", toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInfo {
        private final String productId;
        private final String name;
        private final String invoiceNumber;
        private final String quantity;
        private final String catalogProductId;
        private final String code;
        private final String amount;
        private final String skuCode;
        private final String serialNo;
        private final String manufacturer;
        private final String category;
        private final String subCategory;
        private final String model;
        private final String imeiNo;
        private final Map<String, String> additionalInfo;
        private final String brandId;
        private final Boolean isSerialNumberVerified;
        private final String variant;
        private final List<ProductInfo> productsList;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ProductInfoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = ProductDetailsDto.ProductDetailsDtoBuilder.class)
    @Builder(builderClassName = "ProductDetailsDtoBuilder", toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductDetailsDto {
        private final String productId;
        private final String name;
        private final String invoiceNumber;
        private final String quantity;
        private final String catalogProductId;
        private final String code;
        private final String amount;
        private final String skuCode;
        private final String serialNo;
        private final String manufacturer;
        private final String category;
        private final String subCategory;
        private final String model;
        private final String imeiNo;
        private final Map<String, String> additionalInfo;
        private final String brandId;
        private final Boolean isSerialNumberVerified;
        private final String variant;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ProductDetailsDtoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = LoanInfo.LoanInfoBuilder.class)
    @Builder(builderClassName = "LoanInfoBuilder", toBuilder = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LoanInfo {
        private final String loanId;
        private final String loanStatus;
        private final String loanSubStatus;

        @JsonPOJOBuilder(withPrefix = "")
        public static class LoanInfoBuilder {
        }
    }
}
