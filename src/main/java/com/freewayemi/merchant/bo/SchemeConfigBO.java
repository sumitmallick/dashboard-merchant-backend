package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.BankInterestTypeEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.SchemeConfigRequest;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.service.PaymentOpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.CollectionUtils;


import java.util.*;

@Component
public class SchemeConfigBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemeConfigBO.class);
    private final PaymentOpsService paymentOpsService;

    @Autowired
    public SchemeConfigBO(PaymentOpsService paymentOpsService) {
        this.paymentOpsService = paymentOpsService;
    }

    public Map<String, ProviderMasterConfigInfo> constructProviderMasterConfigMap(String partner) {
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = new HashMap<>();

        List<ProviderMasterConfigInfo> configs = getProviderMasterConfigs(partner);
        if (!CollectionUtils.isEmpty(configs)) {
            configs.forEach(configInfo -> providerMasterConfigInfoMap.put(
                    configInfo.getCardType() + "_" + configInfo.getBankCode(), configInfo));
        }
        return providerMasterConfigInfoMap;
    }

    public List<ProviderMasterConfigInfo> getProviderMasterConfigs(String partner) {
        ProviderMasterConfigResponse providerMasterConfigResponse = paymentOpsService.getAllProviderMasterConfig(partner);
        if (Objects.nonNull(providerMasterConfigResponse) && Objects.nonNull(providerMasterConfigResponse.getProviderMasterConfigs())) {
            return providerMasterConfigResponse.getProviderMasterConfigs();
        }
        return new ArrayList<>();
    }

    public List<InterestPerTenureDto> getSchemeMasterData(String partnerCode, String merchantId, String brandId,
                                                          String productId) {
        return getInterestPerTenureDtoBySchemeConfig(paymentOpsService.getMasterData(
                populateSchemeConfigRequest(partnerCode, merchantId, brandId, productId)));
    }

    public List<InterestPerTenureDto> getInterestPerTenureDtoBySchemeConfig(SchemeConfigResponse response) {
        List<InterestPerTenureDto> interestPerTenureDtoBySchemeConfig = new ArrayList<>();
        if (Objects.isNull(response) || CollectionUtils.isEmpty(response.getConfigData()) || response.getCode() != 0) {
            return interestPerTenureDtoBySchemeConfig;
        }
        response.getConfigData().forEach(configData -> {
            configData.getSchemeData().forEach(schemeData -> {
                schemeData.getTenureConfig()
                        .forEach((tenure, valueObj) -> {
                            if (Objects.nonNull(valueObj)) {
                                InterestPerTenureDto interestPerTenureDto =
                                        buildInterestPerTenureDto(configData.getCardType(), configData.getBankCode(),
                                                Integer.valueOf(tenure), valueObj);
                                interestPerTenureDto.setBankInterestTypeEnum(
                                        Objects.nonNull(valueObj.getBankInterestType()) ? BankInterestTypeEnum.valueOf(
                                                valueObj.getBankInterestType()) : null);
                                interestPerTenureDto.setApplicabilityType(schemeData.getApplicabilityType());
                                interestPerTenureDto.setProviderSchemeId(schemeData.getProviderSchemeId());
                                interestPerTenureDto.setSchemeMappingId(configData.getSchemeMappingId());
                                interestPerTenureDtoBySchemeConfig.add(interestPerTenureDto);
                            }
                        });
            });
        });
        return interestPerTenureDtoBySchemeConfig;
    }

    public List<InterestPerTenureDto> getStandardInterestPerTenureDtoList(String cardType, String bankCode,
                                                                          Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        List<InterestPerTenureDto> tenures = new ArrayList<>();
        if (MapUtils.isEmpty(providerMasterConfigInfoMap)) {
            return tenures;
        }
        ProviderMasterConfigInfo providerMasterConfigInfo = providerMasterConfigInfoMap.get(cardType + "_" + bankCode);
        if (Objects.isNull(providerMasterConfigInfo) || Objects.isNull(providerMasterConfigInfo.getTenureConfig())) {
            return tenures;
        }

        providerMasterConfigInfo.getTenureConfig()
                .forEach((tenure, schemeDetail) -> {
                    if (Objects.nonNull(schemeDetail)) {
                        InterestPerTenureDto interestPerTenureDto =
                                buildInterestPerTenureDto(cardType, bankCode, Integer.valueOf(tenure), schemeDetail);
                        interestPerTenureDto.setBankInterestTypeEnum(BankInterestTypeEnum.STANDARD);
                        tenures.add(interestPerTenureDto);
                    }
                });
        return tenures;
    }

    public InterestPerTenureDto getStandardInterestPerTenure(String cardType, String bankCode, Integer tenure,
                                                             Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        if (MapUtils.isEmpty(providerMasterConfigInfoMap)) {
            return null;
        }
        String effectiveCardType = Util.getEffectiveCardType(bankCode, cardType);
        ProviderMasterConfigInfo providerMasterConfigInfo =
                providerMasterConfigInfoMap.get(effectiveCardType + "_" + bankCode);
        if (Objects.isNull(providerMasterConfigInfo) || Objects.isNull(providerMasterConfigInfo.getTenureConfig())) {
            return null;
        }

        ProviderSchemeDetail schemeDetail = providerMasterConfigInfo.getTenureConfig().get(tenure.toString());
        if (Objects.isNull(schemeDetail)) {
            return null;
        }

        InterestPerTenureDto interestPerTenureDto = buildInterestPerTenureDto(effectiveCardType, bankCode, tenure, schemeDetail);
        interestPerTenureDto.setBankInterestTypeEnum(BankInterestTypeEnum.STANDARD);
        return interestPerTenureDto;
    }

    private SchemeConfigRequest populateSchemeConfigRequest(String partnerCode, String merchantId, String brandId,
                                                            String productId) {
        return SchemeConfigRequest.builder()
                .partnerCode(partnerCode)
                .merchantId(merchantId)
                .brandId(brandId)
                .productId(productId)
                .build();
    }


    private InterestPerTenureDto buildInterestPerTenureDto(String cardType, String bankCode, Integer tenure,
                                                           ProviderSchemeDetail schemeDetail) {
        return InterestPerTenureDto.builder()
                .cardInterestId(schemeDetail.getCardInterestId())
                .cardType(CardTypeEnum.getCardTypeEnum(cardType))
                .bankEnum(BankEnum.getCode(bankCode))
                .tenure(tenure)
                .tenureInDays(tenure * 30)
                .irr(Objects.nonNull(schemeDetail.getBankIrr()) ? Double.valueOf(schemeDetail.getBankIrr()) : null)
                .brandIrr(
                        Objects.nonNull(schemeDetail.getBrandIrr()) ? Double.valueOf(schemeDetail.getBrandIrr()) : null)
                .providerSchemeCode(schemeDetail.getProviderSchemeDetail1())
                .providerDetailedSchemeCode(schemeDetail.getProviderSchemeDetail2())
                .isActive(schemeDetail.getIsActive())
                .validFrom(null)
                .validTo(null)
                .minAmount(Objects.nonNull(schemeDetail.getMinTxnVal()) ? Double.valueOf(schemeDetail.getMinTxnVal())
                        : null)
                .maxAmount(Objects.nonNull(schemeDetail.getMaxTxnVal()) ? Double.valueOf(schemeDetail.getMaxTxnVal())
                        : null)
                .bankPfInPercentage(null)
                .bankPfMaxAmount(null)
                .bankPfFlatAmount(null)
                .actualIrr(Objects.nonNull(schemeDetail.getActualIrr()) ? Double.valueOf(schemeDetail.getActualIrr())
                        : null)
                .calculationType(schemeDetail.getCalculationType())
                .pf(schemeDetail.getPf())
                .build();
    }
}
