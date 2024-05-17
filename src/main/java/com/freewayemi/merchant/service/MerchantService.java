package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.*;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.SettlementCycleEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.MerchantInstantDiscountConfiguration;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.entity.ProviderGroup;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MerchantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantService.class);

    private final MerchantUserBO merchantUserBO;
    private final BrandBO brandBO;
    private final OfferBO offerBO;
    private final BrandProductBO brandProductBO;
    private final BankInterestBO bankInterestBO;
    private final MerchantDiscountRateBO merchantDiscountRateBO;
    private final MerchantInstantDiscountConfigurationBO merchantInstantDisConfigBO;

    @Autowired
    public MerchantService(MerchantUserBO merchantUserBO, BrandBO brandBO, OfferBO offerBO,
                           BrandProductBO brandProductBO, BankInterestBO bankInterestBO,
                           MerchantDiscountRateBO merchantDiscountRateBO,
                           MerchantInstantDiscountConfigurationBO merchantInstantDisConfigBO) {
        this.merchantUserBO = merchantUserBO;
        this.brandBO = brandBO;
        this.offerBO = offerBO;
        this.brandProductBO = brandProductBO;
        this.bankInterestBO = bankInterestBO;
        this.merchantDiscountRateBO = merchantDiscountRateBO;
        this.merchantInstantDisConfigBO = merchantInstantDisConfigBO;
    }

    public MerchantResponse getMerchantDetails(String merchantId, String brandProductId, String serialNumber,
                                               String modelNumber, String merchantProvidedBrandId, String providerGroup,
                                               String productSkuCode) {
        try {
            MerchantUser mu = merchantUserBO.getUserByIdOrDisplayId(merchantId);
            if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
                throw new FreewayException("merchant not approved.");
            }
            MerchantUserResponse mur = new MerchantUserResponse(mu, null,null, brandBO.hasBrand(mu.getParams()), null, null, null, null, null);
            BrandInfo brandInfo = populateBrandInfo(brandProductId, serialNumber, modelNumber, merchantProvidedBrandId, productSkuCode);
            List<MerchantDiscountRateResponse> brandMdrs = null;
            MerchantInstantDiscountConfiguration merchantInstantDisConfig = null;
            String brandId = null != brandInfo ? brandInfo.getBrandId() : null;
            if (StringUtils.hasText(brandId)) {
                brandMdrs = merchantDiscountRateBO.getMerchantDiscountRateByBrandId(brandId);
                merchantInstantDisConfig =
                        merchantInstantDisConfigBO.getMerchantInstantDiscountConfiguration(merchantId, brandId);
            }
            List<PaymentProviderInfo> allowedProviders = getAllowedProviders(mu, providerGroup);
            MerchantResponse merchantResponse = populateMerchantResponse(mur, mu, brandInfo, brandMdrs, merchantInstantDisConfig, allowedProviders);
            return merchantResponse;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting merchant details for id:" + merchantId, e);
        }
        throw new FreewayException("Something went wrong while getting merchant details for id: " + merchantId);
    }

    private BrandInfo populateBrandInfo(String brandProductId, String serialNumber, String modelNumber,
                                        String merchantProvidedBrandId, String productSkuCode) {
        return (StringUtils.hasText(brandProductId) || StringUtils.hasText(modelNumber)) ? brandProductBO.getBrandInfo(
                new BrandRequest(serialNumber, brandProductId, modelNumber, merchantProvidedBrandId, productSkuCode)) : null;
    }

    private MerchantInstantDiscountConfigResp populateMerchantInstantDiscountConfigResp(
            MerchantInstantDiscountConfiguration merchantInstantDisConfig, BrandInfo brandInfo) {
        MerchantInstantDiscountConfigResp merchantInstantDiscountConfigResp = null;
        if (Objects.nonNull(merchantInstantDisConfig)) {
            merchantInstantDiscountConfigResp = MerchantInstantDiscountConfigResp.builder()
                    .merchantId(merchantInstantDisConfig.getMerchantId())
                    .brandId(merchantInstantDisConfig.getBrandId())
                    .offerType(merchantInstantDisConfig.getOfferType())
                    .additionalMdr(merchantInstantDisConfig.getAdditionalMdr())
                    .status(merchantInstantDisConfig.getStatus())
                    .brandFeeRate(brandInfo.getBrandFeeRateInstantDiscount())
                    .build();
        }
        return merchantInstantDiscountConfigResp;
    }

    private MerchantResponse populateMerchantResponse(MerchantUserResponse mur, MerchantUser mu, BrandInfo brandInfo,
                                                      List<MerchantDiscountRateResponse> brandMdrs,
                                                      MerchantInstantDiscountConfiguration merchantInstantDisConfig,
                                                      List<PaymentProviderInfo> allowedProviders) {
        return MerchantResponse.builder()
                .merchantId(mur.getMerchantId())
                .shopName(mur.getShopName())
                .email(mur.getEmail())
                .mobile(mur.getMobile())
                .displayId(mur.getDisplayId())
                .offers(offerBO.getPgMerchantOffers(mu.getId().toString()))
                .deviceToken(mu.getDeviceToken())
                .params(mu.getParams())
                .returnUrl(mu.getReturnUrl())
                .webhookUrl(mu.getWebhookUrl())
                .mdrs(mu.getMdrs())
                .supportedDpProviders(mu.getSupportedDpProviders())
                .downPaymentEnabled(mu.getDownPaymentEnabled())
                .type(mu.getType())
                .qr(mu.getQrCode())
                .category(mu.getCategory())
                .subCategory(mu.getSubCategory())
                .mccCode(mu.getMccCode())
                .isSeamless(mur.getIsSeamless())
                .isConvFee(mu.getIsConvFee())
                .convFeeRates(mu.getConvFeeRates())
                .isGiftVoucherEnabled(mu.getIsGiftVoucherEnabled())
                .isInvoiceEnabled(mu.getIsInvoiceEnabled())
                .isInvoicingModel(mu.getIsInvoicingModel())
                .isBrandSubventionModel(mu.getIsBrandSubventionModel())
                .brandSubventions(getBrandSubventions(mu, brandInfo))
                .address(mu.getAddress())
                .gst(mu.getGst())
                .canCxBuyInsurance(mu.getCanCxBuyInsurance())
                .brandInfo(brandInfo)
                .brandMdrs(brandMdrs)
                .approvedDate(mu.getApprovedDate())
                .appInstalledDate(mu.getAppInstalledDate())
                .ownership(mu.getOwnership())
                .firstName(mu.getFirstName())
                .lastName(mu.getLastName())
                .source(mu.getSource())
                .status(mu.getStatus())
                .createdDate(mu.getCreatedDate())
                .businessName(mu.getBusinessName())
                .qr(mu.getQrCode())
                .settlementConfigDto(SettlementConfigDto.builder()
                        .settlementCycle(Util.isNotNull(mu.getSettlementConfig()) &&
                                Util.isNotNull(mu.getSettlementConfig().getSettlementCycle()) ? mu.getSettlementConfig()
                                .getSettlementCycle() : SettlementCycleEnum.STANDARD)
                        .lyraPgSettlementConfig(Util.isNotNull(mu.getSettlementConfig()) ?
                                new LyraPgSettlementConfigDto(mu.getSettlementConfig().getLyraPgSettlementConfig()) : null)
                        .build())
                .bankInterestDtoOnMerchant(
                        bankInterestBO.getBankInterestByMerchantId(mur.getMerchantId(), mur.getDisplayId()))
                .bankInterestDtoOnBrand(bankInterestBO.getBankInterestByBrandInfo(brandInfo))
                .isInstantCashbackEnabled(merchantInstantDisConfig != null)
                .merchantInstantDiscountConfigResp(
                        populateMerchantInstantDiscountConfigResp(merchantInstantDisConfig, brandInfo))
                .partner(mu.getPartner())
                .parentMerchant(mu.getParentMerchant())
                .masterMerchants(mu.getMasterMerchants())
                .businessName(mu.getBusinessName())
                .allowedProviders(allowedProviders)
                .build();
    }

    private List<OfferResponse> getBrandSubventions(MerchantUser mu, BrandInfo brandInfo) {
        if (Objects.nonNull(brandInfo)) {
            // product specific brand offers
            return offerBO.getBrandSubventionsForProduct(brandInfo.getBrandId(), brandInfo.getBrandProductId(),
                    mu.getId().toString(), mu.getPartner());
        } else if (Objects.nonNull(mu.getParams()) && StringUtils.hasText(mu.getParams().getBrandId())) {
            // other products flow where brandInfo will be null, returning only if brand does not have products
            Brand brand = brandBO.findById(mu.getParams().getBrandId());
            if (brand != null && (brand.getHasProducts() == null || !brand.getHasProducts())) {
                return offerBO.getBrandSubventionsForProduct(mu.getParams().getBrandId(), null,
                        mu.getId().toString(), mu.getPartner());
            }
        }
        return new ArrayList<>();
    }

    public static List<PaymentProviderInfo> getAllowedProviders(MerchantUser mu, String providerGroup) {
        List<PaymentProviderInfo> allowedProviders = null;
        if (StringUtils.hasText(providerGroup)) {
            String groupName = providerGroup.trim();
            allowedProviders = Optional.ofNullable(mu.getProviderGroups()).orElseGet(ArrayList::new).stream()
                    .filter(pg -> groupName.equalsIgnoreCase(pg.getName())).findFirst()
                    .map(ProviderGroup::getProviders)
                    .orElseThrow(() -> new FreewayException("Incorrect providerGroup value passed, please share the correct value"));
            LOGGER.info("Allowed providers for providerGroup: {} are {}", providerGroup, allowedProviders);
        }
        return allowedProviders;
    }

    public static Boolean getProviderGroupEncTxnFlag(MerchantUser mu, String providerGroup) {
        Boolean encTxnLinkEnabled = null;
        if (StringUtils.hasText(providerGroup)) {
            String groupName = providerGroup.trim();
            ProviderGroup providerGroupRes = Optional.ofNullable(mu.getProviderGroups()).orElseGet(ArrayList::new).stream()
                    .filter(pg -> groupName.equalsIgnoreCase(pg.getName())).findFirst()
                    .orElseThrow(() -> new FreewayException("Incorrect providerGroup value passed, please share the correct value"));
            encTxnLinkEnabled = providerGroupRes.getEncTxnLinkEnabled();
        }
        return encTxnLinkEnabled;
    }

}
