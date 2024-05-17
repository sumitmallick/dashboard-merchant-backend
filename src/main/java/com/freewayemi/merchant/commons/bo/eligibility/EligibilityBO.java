package com.freewayemi.merchant.commons.bo.eligibility;

import com.freewayemi.merchant.commons.bo.eligibility.provider.EligibilityProvider;
import com.freewayemi.merchant.commons.bo.eligibility.provider.KotakNtbEligibilityProvider;
import com.freewayemi.merchant.commons.ntbservice.helper.NtbServiceConstants;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class EligibilityBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(EligibilityBO.class);

    private final List<EligibilityProvider> providers;
    private final Boolean isProduction;
    private final Boolean axisPgLive;
    private final Boolean kotakPgLive;
    private final Boolean iciciCardlessPgLive;
    //    realme test number - 7303897042
    private final List<String> NOT_ELIGIBLES = Arrays.asList("9231231230", "9096167416");
    private final KotakNtbEligibilityProvider kotakNtbEligibilityProvider;
    private final Boolean iiflPgLive;

    @Autowired
    public EligibilityBO(List<EligibilityProvider> providers, @Value("${payment.deployment.env}") String env,
                         @Value("${axis.emi.pg.live}") Boolean axisPgLive,
                         @Value("${kotak.emi.pg.live}") Boolean kotakPgLive,
                         KotakNtbEligibilityProvider kotakNtbEligibilityProvider,
                         @Value("${icici.cardless.emi.pg.live}") Boolean iciciCardlessPgLive,
                         @Value("${iifl.emi.pg.live}") Boolean iiflPgLive) {
        providers.sort(Comparator.comparingInt(EligibilityProvider::getScore));
        this.providers = providers;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.axisPgLive = axisPgLive;
        this.kotakPgLive = kotakPgLive;
        this.kotakNtbEligibilityProvider = kotakNtbEligibilityProvider;
        this.iciciCardlessPgLive = iciciCardlessPgLive;
        this.iiflPgLive = iiflPgLive;
    }

    public static boolean isCreditCardEmiAvailable(List<PaymentProviderEnum> supportedProviders) {
        return null != supportedProviders && (supportedProviders.contains(PaymentProviderEnum.juspay) ||
                supportedProviders.contains(PaymentProviderEnum.isgpg) ||
                supportedProviders.contains(PaymentProviderEnum.tpslemipg) ||
                supportedProviders.contains(PaymentProviderEnum.hdfcpg) ||
                supportedProviders.contains(PaymentProviderEnum.razorpayemipg) ||
                supportedProviders.contains(PaymentProviderEnum.cashfreepg) ||
                supportedProviders.contains(PaymentProviderEnum.lyrapg) ||
                supportedProviders.contains(PaymentProviderEnum.ccavenuepg) ||
                supportedProviders.contains(PaymentProviderEnum.ccavenueemipg) ||
                supportedProviders.contains(PaymentProviderEnum.cashfreeemipg));
    }

    public List<String> findAll(List<PaymentProviderEnum> supportedProviders, List<EligibilityResponse> eligibilities) {
        List<String> output = new ArrayList<>();
        if (null != eligibilities) {
            for (EligibilityResponse er : eligibilities) {
                if (er.getEligible() &&
                        isSupportedByMerchantByCardType(supportedProviders, er.getBankCode(), er.getCardType())) {
                    if (BankEnum.KKBK.getCode().equalsIgnoreCase(er.getBankCode()) &&
                            CardTypeEnum.NTB.getCardType().equalsIgnoreCase(er.getCardType())) {
                        continue;
                    }
                    if ((BankEnum.IIFL.getCode().equalsIgnoreCase(er.getBankCode()) ||
                            BankEnum.KKBK.getCode().equalsIgnoreCase(er.getBankCode())) &&
                            (NtbServiceConstants.NTB_NEW_USER.equalsIgnoreCase(er.getEligibleStatus()) ||
                                    NtbServiceConstants.NTB_EXISTING_USER.equalsIgnoreCase(er.getEligibleStatus()) ||
                                    NtbServiceConstants.NTB_PREAPPROVED_USER.equalsIgnoreCase(
                                            er.getEligibleStatus()))) {
                        continue;
                    }
                    if (BankEnum.ICICI_NTB.getCode().equalsIgnoreCase(er.getBankCode()) &&
                            isDcApproved(eligibilities,
                                    Arrays.asList(BankEnum.CL_ICICI.name(), BankEnum.ICIC.name()))) {
                        continue;
                    }
                    if (BankEnum.HDFC_NTB.getCode().equalsIgnoreCase(er.getBankCode()) &&
                            isDcApproved(eligibilities, Collections.singletonList(BankEnum.HDFC.name()))) {
                        continue;
                    }
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.mock) &&
                        ("HDFC".equalsIgnoreCase(er.getBankCode()) || "CL_HDFC".equalsIgnoreCase(er.getBankCode()))) {
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.mockcl) &&
                        "CL_ICICI".equalsIgnoreCase(er.getBankCode())) {
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.kotakmock) &&
                        "KKBK".equalsIgnoreCase(er.getBankCode())) {
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.mockicicikyccardless) &&
                        "CL_ICICI_KYC".equalsIgnoreCase(er.getBankCode())) {
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.mockicicidc) &&
                        BankEnum.ICIC.getCode().equalsIgnoreCase(er.getBankCode())) {
                    output.add(er.getBankCode());
                }
                if (!isProduction && null != supportedProviders &&
                        supportedProviders.contains(PaymentProviderEnum.mockaxisdc) &&
                        BankEnum.UTIB.getCode().equalsIgnoreCase(er.getBankCode())) {
                    output.add(er.getBankCode());
                }
            }
        }
        if (isCreditCardEmiAvailable(supportedProviders)) {
            output.add("CC");
        }
        return output;
    }

    private boolean isSupportedByMerchantByCardType(List<PaymentProviderEnum> supportedProviders, String bankCode,
                                                    String cardType) {
        return null != supportedProviders &&
                supportedProviders.contains(getProviderEnumByCardTypeAndBankCode(cardType, bankCode));
    }

    private PaymentProviderEnum getProviderEnum(String bankCode) {
        switch (bankCode) {
            case "UTIB":
                return PaymentProviderEnum.axis;
            case "HDFC":
            case "CL_HDFC":
                return PaymentProviderEnum.hdfc;
            case "ICIC":
                return PaymentProviderEnum.icici;
            case "KKBK":
                return PaymentProviderEnum.kotak;
            case "IDFC":
                return PaymentProviderEnum.idfc;
            case "CL_ICICI":
                return PaymentProviderEnum.icicicardless;
            case "CL_ICICI_KYC":
                return PaymentProviderEnum.icicikyccardless;
            case "BNPL_HDFC":
                return PaymentProviderEnum.flexipay;
            case "IIFL":
                return PaymentProviderEnum.iifl;
            case "HDFC_NTB":
                return PaymentProviderEnum.hdfcntb;
            case "ICICI_NTB":
                return PaymentProviderEnum.icicintb;
            case "DMI_NTB":
                return PaymentProviderEnum.dmintb;
            default:
                return PaymentProviderEnum.none;
        }
    }

    private PaymentProviderEnum getProviderEnumByCardTypeAndBankCode(String cardType, String bankCode) {
        if (StringUtils.hasText(cardType)) {
            switch (cardType) {
                case "DEBIT":
                    switch (bankCode) {
                        case "HDFC":
                            return PaymentProviderEnum.hdfc;
                        case "KKBK":
                            return PaymentProviderEnum.kotak;
                        case "IDFC":
                            return PaymentProviderEnum.idfc;
                        case "ICIC":
                            return PaymentProviderEnum.icici;
                        case "UTIB":
                            return PaymentProviderEnum.axis;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "CARDLESS":
                    switch (bankCode) {
                        case "CL_ICICI":
                            return PaymentProviderEnum.icicicardless;
                        case "CL_ICICI_KYC":
                            return PaymentProviderEnum.icicikyccardless;
                        case "HDFC":
                            return PaymentProviderEnum.hdfc;
                        case "KKBK":
                            return PaymentProviderEnum.kotak;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "BNPL":
                    switch (bankCode) {
                        case "BNPL_HDFC":
                            return PaymentProviderEnum.flexipay;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "NTB":
                    switch (bankCode) {
                        case "IIFL":
                            return PaymentProviderEnum.iifl;
                        case "KKBK":
                            return PaymentProviderEnum.kotakntb;
                        case "HDFC_NTB":
                            return PaymentProviderEnum.hdfcntb;
                        case "ICICI_NTB":
                            return PaymentProviderEnum.icicintb;
                        case "DMI_NTB":
                            return PaymentProviderEnum.dmintb;
                        default:
                            return PaymentProviderEnum.none;
                    }
                default:
                    return PaymentProviderEnum.none;
            }
        }
        return getProviderEnum(bankCode);
    }

    private boolean isDcApproved(List<EligibilityResponse> eligibilities, List<String> bankEnums) {
        return eligibilities.stream()
                .anyMatch(eligibilityResponse -> (BooleanUtils.isTrue(eligibilityResponse.getEligible()) &&
                        bankEnums.contains(eligibilityResponse.getBankCode())));
    }

    public List<EligibilityResponse> createAllEligibilities(List<PaymentProviderEnum> supportedProviders,
                                                            List<CardTypeEnum> cardTypes) {
        List<EligibilityResponse> eligibilityResponses = new ArrayList<>();

        if (CollectionUtils.isEmpty(cardTypes)) {
            cardTypes = Arrays.asList(CardTypeEnum.values());
        }

        for (CardTypeEnum cardType: cardTypes) {
            for (BankEnum bank: BankEnum.values()) {
                if (isBankCodeCardTypeSupportedByMerchant(supportedProviders, cardType.getCardType(), bank.getCode())) {
//                    if (CardTypeEnum.NTB.equals(cardType) && BankEnum.KKBK.equals(bank)) {
//                        // skipping Kotak NTB.
//                        continue;
//                    }
                    eligibilityResponses.add(
                            EligibilityResponse.builder()
                            .cardType(cardType.getCardType())
                            .bankCode(bank.getCode())
                            .eligible(Boolean.TRUE)
                            .build()
                    );
                }
            }
        }

        return eligibilityResponses;
    }

    private boolean isBankCodeCardTypeSupportedByMerchant(List<PaymentProviderEnum> supportedProviders, String cardType,
                                                          String bankCode) {

        if (supportedProviders == null) {
            return false;
        }
        PaymentProviderEnum providerEnum = getProviderEnumByCardTypeAndBankCode(cardType, bankCode);
        if (PaymentProviderEnum.none.equals(providerEnum)) {
            return false;
        }
        if (supportedProviders.contains(providerEnum)) {
            return true;
        }
        if (!isProduction) {
            providerEnum = getMockProviderByCardTypeAndBankCode(cardType, bankCode);
            return !PaymentProviderEnum.none.equals(providerEnum) && supportedProviders.contains(providerEnum);
        }
        return false;
    }

    private PaymentProviderEnum getMockProviderByCardTypeAndBankCode(String cardType, String bankCode) {
        if (StringUtils.hasText(cardType)) {
            switch (cardType) {
                case "DEBIT":
                    switch (bankCode) {
                        case "HDFC":
                            return PaymentProviderEnum.mock;
                        case "KKBK":
                            return PaymentProviderEnum.kotakmock;
                        case "ICIC":
                            return PaymentProviderEnum.mockicicidc;
                        case "UTIB":
                            return PaymentProviderEnum.mockaxisdc;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "CARDLESS":
                    switch (bankCode) {
                        case "CL_ICICI":
                            return PaymentProviderEnum.mockcl;
                        case "CL_ICICI_KYC":
                            return PaymentProviderEnum.mockicicikyccardless;
                        case "HDFC":
                            return PaymentProviderEnum.mock;
                        default:
                            return PaymentProviderEnum.none;
                    }
                default:
                    return PaymentProviderEnum.none;
            }
        }
        return PaymentProviderEnum.none;
    }
}
