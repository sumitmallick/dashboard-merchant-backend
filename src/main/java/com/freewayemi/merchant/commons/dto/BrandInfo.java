package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.entity.BrandParams;
import com.freewayemi.merchant.commons.type.EMIOfferType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class BrandInfo {
    private String brandId;
    private String serialNumber;
    private String brandProductId;
    private String icon;
    private String sideBanner;
    private String emiOption;
    private String product;
    private String variant;
    private String modelNumber;
    private String name;
    private String productSkuId;
    private Integer paymentCycle;
    private Float amount;
    private Float minAmount;
    private Boolean fraudChecks;
    private Integer purchaseVelocity;
    private Boolean isBrandMdrModel;
    private String subventionType;
    private String referralCode;
    private String schemeId;
    private String brandDisplayId;
    private EMIOfferType emiOfferType;
    private BrandParams brandParams;
    private String category;
    private Boolean isBrandAdditionalCashbackValidationModel;
    private Float brandFeeRateInstantDiscount;
    private MarginMoneyConfigDto marginMoneyConfig;
    private String brandProductSku;
    private String brandAPI;
    private Boolean asyncReport;
    private Boolean asyncUnclaim;
    private Boolean barcodeScanEnabled;
    private Boolean hideProductAmount;
    private Boolean freezePaymentModeOnSerialNumber;
    private String productCategory;
    private VelocityConfigDto velocityConfig;

    @JsonCreator
    public BrandInfo(@JsonProperty("brandId") String brandId, @JsonProperty("serialNumber") String serialNumber,
                     @JsonProperty("brandProductId") String brandProductId, @JsonProperty("icon") String icon,
                     @JsonProperty("sideBanner") String sideBanner, @JsonProperty("emiOption") String emiOption,
                     @JsonProperty("product") String product, @JsonProperty("variant") String variant,
                     @JsonProperty("modelNumber") String modelNumber, @JsonProperty("name") String name,
                     @JsonProperty("productSkuId") String productSkuId,
                     @JsonProperty("paymentCycle") Integer paymentCycle, @JsonProperty("amount") Float amount,
                     @JsonProperty("minAmount") Float minAmount, @JsonProperty("fraudChecks") Boolean fraudChecks,
                     @JsonProperty("purchaseVelocity") Integer purchaseVelocity,
                     @JsonProperty("isBrandMdrModel") Boolean isBrandMdrModel,
                     @JsonProperty("subventionType") String subventionType,
                     @JsonProperty("referralCode") String referralCode, @JsonProperty("schemeId") String schemeId,
                     @JsonProperty("brandDisplayId") String brandDisplayId,
                     @JsonProperty("emiOfferType") EMIOfferType emiOfferType,
                     @JsonProperty("brandParams") BrandParams brandParams, @JsonProperty("category") String category,
                     @JsonProperty("isBrandAdditionalCashbackValidationModel")
                     Boolean isBrandAdditionalCashbackValidationModel,
                     @JsonProperty("brandFeeRateInstantDiscount") Float brandFeeRateInstantDiscount,
                     @JsonProperty("marginMoneyConfig") MarginMoneyConfigDto marginMoneyConfig,
                     @JsonProperty("brandProductSku") String brandProductSku,
                     @JsonProperty("brandAPI") String brandAPI,
                     @JsonProperty("asyncReport") Boolean asyncReport,
                     @JsonProperty("asyncUnclaim") Boolean asyncUnclaim,
                     @JsonProperty("barcodeScanEnabled") Boolean barcodeScanEnabled,
                     @JsonProperty("hideProductAmount") Boolean hideProductAmount,
                     @JsonProperty("freezePaymentModeOnSerialNumber") Boolean freezePaymentModeOnSerialNumber,
                     @JsonProperty("productCategory") String productCategory,
                     @JsonProperty("velocityConfig") VelocityConfigDto velocityConfig) {
        this.brandId = brandId;
        this.serialNumber = serialNumber;
        this.brandProductId = brandProductId;
        this.icon = icon;
        this.sideBanner = sideBanner;
        this.emiOption = emiOption;
        this.product = product;
        this.variant = variant;
        this.modelNumber = modelNumber;
        this.name = name;
        this.productSkuId = productSkuId;
        this.paymentCycle = paymentCycle;
        this.amount = amount;
        this.minAmount = minAmount;
        this.fraudChecks = fraudChecks;
        this.purchaseVelocity = purchaseVelocity;
        this.isBrandMdrModel = isBrandMdrModel;
        this.subventionType = subventionType;
        this.referralCode = referralCode;
        this.schemeId = schemeId;
        this.brandDisplayId = brandDisplayId;
        this.emiOfferType = emiOfferType;
        this.brandParams = brandParams;
        this.category = category;
        this.isBrandAdditionalCashbackValidationModel = isBrandAdditionalCashbackValidationModel;
        this.brandFeeRateInstantDiscount = brandFeeRateInstantDiscount;
        this.marginMoneyConfig = marginMoneyConfig;
        this.brandProductSku = brandProductSku;
        this.brandAPI = brandAPI;
        this.asyncReport = asyncReport;
        this.asyncUnclaim = asyncUnclaim;
        this.barcodeScanEnabled = barcodeScanEnabled;
        this.hideProductAmount = hideProductAmount;
        this.freezePaymentModeOnSerialNumber =freezePaymentModeOnSerialNumber;
        this.productCategory = productCategory;
        this.velocityConfig = velocityConfig;
    }
}
