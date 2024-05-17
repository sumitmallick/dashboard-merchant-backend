package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonDeserialize(builder = MerchantResponse.MerchantResponseBuilder.class)
@Builder(builderClassName = "MerchantResponseBuilder", toBuilder = true)
public class MerchantResponse {
    private final String merchantId;
    private final String shopName;
    private final List<OfferResponse> offers;
    private final String mobile;
    private final String email;
    private final String displayId;
    private final String deviceToken;
    private final String returnUrl;
    private final String webhookUrl;
    private final Params params;
    private final List<Mdr> mdrs;
    private final List<PaymentProviderEnum> supportedProviders;
    private final List<PaymentProviderInfo> supportedDpProviders;
    private final List<PaymentProviderInfo> allowedProviders;
    private final Boolean downPaymentEnabled;
    private final String qr;
    private final String type;
    private final String category;
    private final String subCategory;
    private final String mccCode;
    private final Boolean isSeamless;
    private final Boolean canCxBuyInsurance;
    private final Boolean isConvFee;
    private final List<ConvFeeRate> convFeeRates;
    private final List<MarketingMerchant> marketingMerchants;
    private final Boolean isGiftVoucherEnabled;
    private final Boolean isInvoiceEnabled;
    private final Boolean isInvoicingModel;
    private final Boolean isBrandSubventionModel;
    private final List<OfferResponse> brandSubventions;
    private final Address address;
    private final String gst;
    private final BrandInfo brandInfo;
    private final List<MerchantDiscountRateResponse> brandMdrs;
//    private final BrandPaymentConfigDto brandPaymentConfigDto;
    private final Instant approvedDate;
    private final Instant appInstalledDate;
    private final String ownership;
    private final String firstName;
    private final String lastName;
    private final String source;
    private final String status;
    private final Instant createdDate;
    private final String businessName;
    private final SettlementConfigDto settlementConfigDto;
    private final BankInterestDto bankInterestDtoOnMerchant;
    private final BankInterestDto bankInterestDtoOnBrand;
    private final Boolean isInstantCashbackEnabled;
    private final MerchantInstantDiscountConfigResp merchantInstantDiscountConfigResp;
    private final String partner;
    private final String parentMerchant;
    private final String masterMerchants;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MerchantResponseBuilder {
    }
}
