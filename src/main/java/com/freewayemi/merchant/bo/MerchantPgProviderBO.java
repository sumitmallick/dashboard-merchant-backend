package com.freewayemi.merchant.bo;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.amazonaws.services.dynamodbv2.xspec.L;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.PaymentModeEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.controller.MerchantPgProviderController;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.PaymentOpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MerchantPgProviderBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPgProviderController.class);

    private final PaymentServiceBO paymentServiceBO;
    private final PaymentOpsService paymentOpsService;
    private final String URL_TEMPLATE = "https://paymentassets.s3.ap-south-1.amazonaws.com/logos/%s.png";

    @Autowired
    public MerchantPgProviderBO(PaymentServiceBO paymentServiceBO, PaymentOpsService paymentOpsService) {
        this.paymentServiceBO = paymentServiceBO;
        this.paymentOpsService = paymentOpsService;
    }

    public Boolean hasMerchantRestricted(Params merchantParams, CardTypeEnum cardTypeEnum, String code, String mccCode, Boolean downpaymentEnabled) {
        if ("5571".equals(mccCode)) {
            if (BankEnum.HDFC.getCode().equalsIgnoreCase(code) &&
                    (CardTypeEnum.DEBIT.equals(cardTypeEnum) || CardTypeEnum.CARDLESS.equals(cardTypeEnum))) {
                if (null == downpaymentEnabled || !downpaymentEnabled) {
                    return true;
                }
            }
        }
        if (null != merchantParams && null != merchantParams.getExclusionPaymentTypes() && null != cardTypeEnum) {
            List<String> exclusionPaymentTypes = Arrays.asList(merchantParams.getExclusionPaymentTypes().split(","));
            if(exclusionPaymentTypes.contains(cardTypeEnum.getCardType())) {
                return true;
            }
        }
        if (CardTypeEnum.DEBIT.equals(cardTypeEnum) && null != merchantParams && null != merchantParams.getExclusionDebitBanks() && null != code) {
            List<String> exclusionDebitBanks = Arrays.asList(merchantParams.getExclusionDebitBanks().split(","));
            if(exclusionDebitBanks.contains(code)) {
                return true;
            }
        } else if (CardTypeEnum.CREDIT.equals(cardTypeEnum) && null != merchantParams && null != merchantParams.getExclusionCreditBanks() && null != code) {
            List<String> exclusionCreditBanks = Arrays.asList(merchantParams.getExclusionCreditBanks().split(","));
            if(exclusionCreditBanks.contains(code)){
                return true;
                }
            }
        return false;
    }

    public SupportedBankResponse getSupportedBanks(String merchantId) {
        List<BankEnum> creditBankList = Arrays.asList(BankEnum.HDFC, BankEnum.ICIC, BankEnum.UTIB, BankEnum.CITI,
                BankEnum.RATN, BankEnum.INDB, BankEnum.KKBK, BankEnum.BARB, BankEnum.SCBL, BankEnum.AMEX);
        List<BankDTO> creditBanks = creditBankList.stream().map(bank ->
                new BankDTO(bank.getCode(), bank.getBankName(), String.format(URL_TEMPLATE, bank.getCode())))
                .collect(Collectors.toList());
        List<BankDTO> debitBanks = new ArrayList<>();
        if (StringUtils.hasText(merchantId)) {
            List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(merchantId);
            if (!providers.isEmpty()) {
                providers.forEach(provider -> {
                    if (null != provider.getDisabled() && !provider.getDisabled()) {
                        if (PaymentModeEnum.DEBIT.equals(provider.getType()) && null != provider.getBank()) {
                            debitBanks.add(new BankDTO(provider.getBank().getCode(),
                                    provider.getBank().getBankName(),
                                    String.format(URL_TEMPLATE, provider.getBank().getCode())));
                        }
                    }
                });
            }
        }
        List<BankDTO> cardlessBanks = new ArrayList<>();
        if (StringUtils.hasText(merchantId)) {
            List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(merchantId);
            if (!providers.isEmpty()) {
                providers.forEach(provider -> {
                    if (null != provider.getDisabled() && !provider.getDisabled()) {
                        if (PaymentModeEnum.CARDLESS.equals(provider.getType()) && null != provider.getBank()) {
                            cardlessBanks.add(new BankDTO(provider.getBank().getCode(),
                                    provider.getBank().getBankName(),
                                    String.format(URL_TEMPLATE, provider.getBank().getCode())));
                        }
                    }
                });
            }
        }
        return new SupportedBankResponse(creditBanks, debitBanks, cardlessBanks);
    }

    public SupportedProvidersResponse getSupportedProviders(MerchantUser merchantUser){
        String merchantId = merchantUser.getId().toString();

        List<BankDTO> debitBanks = new ArrayList<>();
        List<BankDTO> creditBanks = new ArrayList<>();
        List<BankDTO> cardlessBanks = new ArrayList<>();
        List<BankDTO> ntbBanks = new ArrayList<>();

        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(merchantId);

        PaymentProviderResponse paymentProviderResponse = paymentOpsService.getAllProviderMappings();

        providers.forEach(provider -> {

            Params params = merchantUser.getParams();
            Boolean isDownpaymentEnabled = Boolean.FALSE;
            String mccCode = "";
            String bankCode = "";

                if (Util.isNotNull(merchantUser.getDownPaymentEnabled())) {
                    isDownpaymentEnabled = Boolean.TRUE;
                }

                if (StringUtils.hasText(merchantUser.getMccCode())) {
                    mccCode = merchantUser.getMccCode();
                }
                if(Util.isNotNull(provider.getBank()) && StringUtils.hasText(provider.getBank().getCode())){
                    bankCode = provider.getBank().getCode();
                }

                if (Objects.nonNull(provider.getDisabled()) && !provider.getDisabled()) {
                    Boolean validateProviderMappings = validateProviderMapping(paymentProviderResponse, provider);
                    Boolean validateExclusionType = hasMerchantRestricted(params, CardTypeEnum.getCardTypeEnum(provider.getType().getDisplayMsg()), bankCode, mccCode, isDownpaymentEnabled);

                    if (Util.isNotNull(provider.getType())) {
                        switch (provider.getType()) {
                            case CREDIT:
                                if (!Util.isNotNull(provider.getBank())) {
                                    List<SupportedBankInfo> allCreditList = fetchCreditBanksForProvider(provider,paymentProviderResponse);
                                    for (SupportedBankInfo allCredit : allCreditList) {
                                        if (!allCredit.getCode().equals("ALL") && !isDuplicateProvider(creditBanks, allCredit.getCode()) && !hasMerchantRestricted(params, CardTypeEnum.CREDIT, allCredit.getCode(), mccCode, isDownpaymentEnabled)) {
                                            BankDTO bankDTO = new BankDTO(allCredit.getCode(),
                                                    allCredit.getDisplayName(),
                                                    String.format(URL_TEMPLATE, allCredit.getCode()));
                                            creditBanks.add(bankDTO);
                                        }
                                    }
                                } else {
                                    if (!validateExclusionType && validateProviderMappings && Objects.nonNull(provider.getBank()) && !isDuplicateProvider(creditBanks, provider.getBank().getCode())) {
                                        creditBanks.add(getBankDtoFromProvider(provider));
                                    }
                                }
                                break;
                            case DEBIT:
                                if (!validateExclusionType && validateProviderMappings && Objects.nonNull(provider.getBank()) && !isDuplicateProvider(debitBanks, provider.getBank().getCode())) {
                                    debitBanks.add(getBankDtoFromProvider(provider));
                                }
                                break;
                            case CARDLESS:
                                if (!validateExclusionType && validateProviderMappings && Objects.nonNull(provider.getBank()) && !isDuplicateProvider(cardlessBanks, provider.getBank().getCode())) {
                                    cardlessBanks.add(getBankDtoFromProvider(provider));
                                }
                                break;
                            case NTB:
                                if (!validateExclusionType && validateProviderMappings && Objects.nonNull(provider.getBank()) && !isDuplicateProvider(ntbBanks, provider.getBank().getCode())) {
                                    ntbBanks.add(getBankDtoFromProvider(provider));
                                }
                                break;
                        }
                    }
                }
            });

        return SupportedProvidersResponse.builder()
                .debitBanks(debitBanks)
                .creditBanks(creditBanks)
                .cardlessBanks(cardlessBanks)
                .ntbBanks(ntbBanks)
                .build();
    }

    public BankDTO getBankDtoFromProvider(PaymentProviderInfo providerInfo){
        return new BankDTO(providerInfo.getBank().getCode(),
                providerInfo.getBank().getBankName(),
                String.format(URL_TEMPLATE, providerInfo.getBank().getCode()));
    }

    public Boolean validateProviderMapping(PaymentProviderResponse paymentProviderResponse,PaymentProviderInfo providerInfo){
        if(Util.isNotNull(paymentProviderResponse)){
            if(Util.isNotNull(paymentProviderResponse.getPpcbMappings())) {
                    List<PPCBMappings> paymentProviderInfos = paymentProviderResponse.getPpcbMappings();
                    for (PPCBMappings ppcbMappings : paymentProviderInfos) {
                        if (Objects.nonNull(ppcbMappings) && Util.isNotNull(ppcbMappings.getPaymentProviderInfo())){
                            PaymentProviderInfos paymentProviderInfo = ppcbMappings.getPaymentProviderInfo();
                            if (StringUtils.hasText(providerInfo.getProvider().getDisplayName()) && StringUtils.hasText(paymentProviderInfo.getCode()) && paymentProviderInfo.getCode().equals(providerInfo.getProvider().getDisplayName())) {
                                if (Util.isNotNull(paymentProviderInfo.getSupportedCardTypeAndBankInfo())) {
                                    for (SupportedCardTypeAndBankInfo supportedCardTypeAndBankInfo : paymentProviderInfo.getSupportedCardTypeAndBankInfo()) {
                                        if (Objects.nonNull(supportedCardTypeAndBankInfo) && supportedCardTypeAndBankInfo.getCode().equals(providerInfo.getType().getDisplayMsg())) {
                                            List<SupportedBankInfo> supportedBankInfo = supportedCardTypeAndBankInfo.getSupportedBankInfo();
                                            for (SupportedBankInfo supportedBankInfo1 : supportedBankInfo) {
                                                if (Objects.nonNull(supportedBankInfo1) && Util.isNotNull(providerInfo.getBank()) && StringUtils.hasText(providerInfo.getBank().getCode()) && supportedBankInfo1.getCode().equals(providerInfo.getBank().getCode())) {
                                                    return Boolean.TRUE;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isDuplicateProvider(List<BankDTO> bankLists,String code){
        for(BankDTO bankDTO:bankLists){
            if(bankDTO.getCode().equals(code)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public List<SupportedBankInfo> fetchCreditBanksForProvider(PaymentProviderInfo providerInfo, PaymentProviderResponse paymentProviderResponse){
        if(Util.isNotNull(paymentProviderResponse.getPpcbMappings())){
            List<PPCBMappings> ppcbMappings = paymentProviderResponse.getPpcbMappings();
            for(PPCBMappings ppcbMappings1 : ppcbMappings){
                if(Objects.nonNull(ppcbMappings1) && Util.isNotNull(ppcbMappings1.getPaymentProviderInfo())) {
                    PaymentProviderInfos paymentProviderInfos = ppcbMappings1.getPaymentProviderInfo();
                    if(Objects.nonNull(paymentProviderInfos) && Objects.nonNull(paymentProviderInfos.getCode()) &&
                            Objects.nonNull(providerInfo) && Objects.nonNull(providerInfo.getProvider()) &&
                            paymentProviderInfos.getCode().equals(providerInfo.getProvider().getDisplayName())) {
                        if (Util.isNotNull(paymentProviderInfos.getSupportedCardTypeAndBankInfo())) {
                            List<SupportedCardTypeAndBankInfo> supportedCardTypeAndBankInfos = paymentProviderInfos.getSupportedCardTypeAndBankInfo();
                            for (SupportedCardTypeAndBankInfo supportedCardTypeAndBankInfo : supportedCardTypeAndBankInfos) {
                                if (Objects.nonNull(supportedCardTypeAndBankInfo) && Util.isNotNull(supportedCardTypeAndBankInfo.getSupportedBankInfo())) {
                                    return supportedCardTypeAndBankInfo.getSupportedBankInfo();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}