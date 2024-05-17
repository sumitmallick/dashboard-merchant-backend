package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.bo.SchemeConfigBO;
import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.bo.brms.OfferBrmsBO;
import com.freewayemi.merchant.commons.bo.brms.Output;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.bo.validators.conditions.PaymentProviderConditionExecuter;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.downpayment.DownPaymentConfigDto;
import com.freewayemi.merchant.commons.dto.downpayment.DownPaymentRulesDto;
import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigInfo;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.type.PartnerCodeEnum;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PaymentOptionsBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOptionsBO.class);

    private final OfferBrmsBO offerBrmsBO;
    private final ConvenienceFeeBO convenienceFeeBO;
    private final CashbackBO cashbackBO;
    private final PaymentProviderConditionExecuter paymentProviderConditionExecuter;
    private final SchemeConfigBO schemeConfigBO;

    private final String minConvenienceFlatFee;
    private final String maxConvenienceFlatFee;
    private final String cutoffTransactionAmount;

    @Autowired
    public PaymentOptionsBO(OfferBrmsBO offerBrmsBO, ConvenienceFeeBO convenienceFeeBO, CashbackBO cashbackBO,
                            PaymentProviderConditionExecuter paymentProviderConditionExecuter, SchemeConfigBO schemeConfigBO,
                            @Value("${convenience.flat.fee.for.amount.less.than.cutoff.amount}")
                            String minConvenienceFlatFee,
                            @Value("${convenience.flat.fee.for.amount.more.than.cutoff.amount}")
                            String maxConvenienceFlatFee,
                            @Value("${convenience.fee.cutoff.transaction.amount}") String cutoffTransactionAmount) {

        this.offerBrmsBO = offerBrmsBO;
        this.convenienceFeeBO = convenienceFeeBO;
        this.cashbackBO = cashbackBO;
        this.paymentProviderConditionExecuter = paymentProviderConditionExecuter;
        this.schemeConfigBO = schemeConfigBO;
        this.minConvenienceFlatFee = minConvenienceFlatFee;
        this.maxConvenienceFlatFee = maxConvenienceFlatFee;
        this.cutoffTransactionAmount = cutoffTransactionAmount;
    }

    public PriceResponse getPgPriceResponse(String cardType, String bankCode, Integer tenure, Float discount,
                                            Float amount, Float splitAmount, Float cFee, Float cashback,
                                            Instant expectedCashbackDate, Boolean subventGst, Float processingFeeRate,
                                            Float maxProcessingFee, String ccieName, Float irrpa, int advanceEmiTenure,
                                            boolean subventProcessingFee, ProviderParams providerParams, String calculationType) {
        Float effectiveAmount = null != splitAmount && splitAmount > 0.0f ? splitAmount : amount;
        PriceResponse pgPriceResponse;
        PriceResponse upr = subventGst ? Util.getGSTCardPriceResponse("pr.getOfferId()", cardType, bankCode, tenure,
                effectiveAmount, cFee, cashback, expectedCashbackDate, processingFeeRate, maxProcessingFee, irrpa,
                providerParams)
                : Util.getCardPriceResponse("pr.getOfferId()", cardType, bankCode, tenure, effectiveAmount, cFee,
                cashback, expectedCashbackDate, subventGst, processingFeeRate, maxProcessingFee, irrpa,
                subventProcessingFee, discount, providerParams);
        if (subventGst) {
            pgPriceResponse = upr;
        } else if (null != upr && upr.getDiscount() < discount) {
            pgPriceResponse = upr;
        } else {
            pgPriceResponse =
                    Util.noCostEmiAmountManual(cFee, cashback, expectedCashbackDate, tenure, irrpa, cardType, ccieName,
                            discount, effectiveAmount - discount, subventGst, processingFeeRate, maxProcessingFee,
                            advanceEmiTenure, subventProcessingFee, discount, providerParams, calculationType);
        }
        if (null != splitAmount && splitAmount > 0.0f) {
            return PriceResponse.builder()
                    .offerId(pgPriceResponse.getOfferId())
                    .tenure(pgPriceResponse.getTenure())
                    .amount(pgPriceResponse.getAmount())
                    .discount(pgPriceResponse.getDiscount())
                    .bankCharges(pgPriceResponse.getBankCharges())
                    .emi(pgPriceResponse.getEmi())
                    .irr(pgPriceResponse.getIrr())
                    .effectiveIrr(pgPriceResponse.getEffectiveIrr())
                    .convFee(cFee)
                    .cashback(Math.min(cashback, pgPriceResponse.getInterestWithoutCFee()))
                    .expectedCashbackDate(expectedCashbackDate)
                    .downPayment(amount - splitAmount)
                    .interestWithoutCFee(pgPriceResponse.getInterestWithoutCFee())
                    .build();
        }
        return pgPriceResponse;
    }

    public PriceResponse getPgPriceResponse(String cardType, String bankCode, Integer tenure, Float discount,
                                            Float amount, Float splitAmount, Float cFee, Float cashback,
                                            Instant expectedCashbackDate, Boolean subventGst, Float processingFeeRate,
                                            Float maxProcessingFee, String ccieName, Float irrpa, int advanceEmiTenure,
                                            boolean subventProcessingFee, ProviderParams providerParams) {
        Float effectiveAmount = null != splitAmount && splitAmount > 0.0f ? splitAmount : amount;
        PriceResponse pgPriceResponse;
        PriceResponse upr = subventGst ? Util.getGSTCardPriceResponse("pr.getOfferId()", cardType, bankCode, tenure,
                effectiveAmount, cFee, cashback, expectedCashbackDate, processingFeeRate, maxProcessingFee, irrpa,
                providerParams)
                : Util.getCardPriceResponse("pr.getOfferId()", cardType, bankCode, tenure, effectiveAmount, cFee,
                cashback, expectedCashbackDate, subventGst, processingFeeRate, maxProcessingFee, irrpa,
                subventProcessingFee, discount, providerParams);
        if (subventGst) {
            pgPriceResponse = upr;
        } else if (null != upr && upr.getDiscount() < discount) {
            pgPriceResponse = upr;
        } else {
            pgPriceResponse =
                    Util.noCostEmiAmountManual(cFee, cashback, expectedCashbackDate, tenure, irrpa, cardType, ccieName,
                            discount, effectiveAmount - discount, subventGst, processingFeeRate, maxProcessingFee,
                            advanceEmiTenure, subventProcessingFee, discount, providerParams);
        }
        if (null != splitAmount && splitAmount > 0.0f) {
            return PriceResponse.builder()
                    .offerId(pgPriceResponse.getOfferId())
                    .tenure(pgPriceResponse.getTenure())
                    .amount(pgPriceResponse.getAmount())
                    .discount(pgPriceResponse.getDiscount())
                    .bankCharges(pgPriceResponse.getBankCharges())
                    .emi(pgPriceResponse.getEmi())
                    .irr(pgPriceResponse.getIrr())
                    .effectiveIrr(pgPriceResponse.getEffectiveIrr())
                    .convFee(cFee)
                    .cashback(cashback)
                    .expectedCashbackDate(expectedCashbackDate)
                    .downPayment(amount - splitAmount)
                    .build();
        }
        return pgPriceResponse;
    }

    /*
     * Below function will take transaction response, bank code, card type, emi tenure
     * and will return the following price response
     * */


    public PriceResponse fixDownpaymentFlowByInterestPerTenure(int tenure, Double irr, Double brandIrr,
                                                               String interestPerTenueId, Float convFee, Float cashback,
                                                               Instant expectedCashbackDate, String cardType,
                                                               float discount, float price, Boolean subventGst,
                                                               float processingFeeRate, float maxProcessingFee,
                                                               boolean subventProcessingFee, String bankCode,
                                                               ProviderParams providerParams, Integer effectiveTenure) {
        float irrpa = cashback > 0 && null != brandIrr ? brandIrr.floatValue() : irr.floatValue();
        int advanceEmiTenure = tenure - effectiveTenure;
        float downpayment = (price / tenure) * advanceEmiTenure;
        float effectivePrincipal = price - downpayment;
        PriceResponse response =
                getPgPriceResponse(cardType, bankCode, effectiveTenure, discount, effectivePrincipal, null, convFee,
                        cashback, expectedCashbackDate, subventGst, processingFeeRate, maxProcessingFee,
                        interestPerTenueId, irrpa, advanceEmiTenure, subventProcessingFee, providerParams);
        return Util.noCostEmiAmountManual(convFee, cashback, expectedCashbackDate, effectiveTenure, irrpa, cardType,
                interestPerTenueId, response.getDiscount(), effectivePrincipal - response.getDiscount(), downpayment,
                subventGst, processingFeeRate, maxProcessingFee, advanceEmiTenure, subventProcessingFee, discount,
                providerParams);
    }


    private boolean isPaymentProviderTenureRestricted(BankEnum bankEnum, String effectiveCardType,
                                                      InterestPerTenureDto interestPerTenureDto, TransactionResponse tr,
                                                      CardTypeEnum cardTypeEnum, PriceResponse response) {
        Boolean subventGst = Util.isSubventGst(cardTypeEnum, tr);
        float pgAmount = getPgAmount(tr, response, subventGst);
        return paymentProviderConditionExecuter.executeTenureRestrictedCondition(pgAmount, bankEnum,
                interestPerTenureDto.getTenure(), effectiveCardType);
    }

    /*
     * ON Cases Like HDFC and Kotak Bank
     * transactions between 3000 - 5000 only Tenure upto 3 months are applicable for the transaction
     * transactions between 5000 - 8000 only Tenure upto 6 months are applicable for the transaction
     * */
    private boolean isEmiTenureRestrictedByPaymentProvider(TransactionResponse transactionResponse, BankEnum bankEnum,
                                                           CardTypeEnum cardTypeEnum, String effectiveCardType,
                                                           Integer effectiveTenure, PriceResponse response) {
        Boolean subventGst = Util.isSubventGst(cardTypeEnum, transactionResponse);
        float pgAmount = getPgAmount(transactionResponse, response, subventGst);
        return paymentProviderConditionExecuter.executeTenureRestrictedCondition(pgAmount, bankEnum, effectiveTenure,
                effectiveCardType);
    }

    private boolean showIciciCardless18MonthsTenure(String showIciciCardless18Months, Integer month,
                                                    BankEnum bankEnum) {
        if (BankEnum.CL_ICICI.equals(bankEnum) && 18 == month) {
            return !StringUtils.isEmpty(showIciciCardless18Months) &&
                    paymentConstants.TRUE.equalsIgnoreCase(showIciciCardless18Months);
        }
        return true;
    }

    private boolean showHdfcDcEmi24MonthsTenure(String showHdfcDcEmi24Months, Integer month, BankEnum bankEnum,
                                                CardTypeEnum cardTypeEnum) {
        if ((CardTypeEnum.CARDLESS.equals(cardTypeEnum) || CardTypeEnum.DEBIT.equals(cardTypeEnum)) &&
                BankEnum.HDFC.equals(bankEnum) && 24 == month) {
            return !StringUtils.isEmpty(showHdfcDcEmi24Months) &&
                    paymentConstants.TRUE.equalsIgnoreCase(showHdfcDcEmi24Months);
        }
        return true;
    }

    private Integer getBanksMaxEligibilityTenure(AdditionInfo additionInfo, BankEnum bankEnum) {
        if (!StringUtils.isEmpty(additionInfo) && null != additionInfo.getBanksMaxEligibilityTenure() &&
                !additionInfo.getBanksMaxEligibilityTenure().isEmpty()) {
            Optional<BanksMaxEligibilityTenure> optional = additionInfo.getBanksMaxEligibilityTenure()
                    .stream()
                    .filter(bank -> bankEnum.getCode().equalsIgnoreCase(bank.getBankCode()))
                    .findAny();
            return optional.map(BanksMaxEligibilityTenure::getMaxEligibleTenure).orElse(null);
        }
        return null;
    }

    private boolean hasTenuresRestricted(Params merchantParams, Integer maxTenure, Integer dcMaxTenure,
                                         Integer ccMaxTenure, CardTypeEnum cardTypeEnum, Integer month,
                                         Integer maxEligibleTenure) {
        if (null != maxEligibleTenure && null != maxTenure) {
            return month > Math.min(maxEligibleTenure, maxTenure);
        }
        if (null != maxTenure && month > maxTenure) {
            return true;
        }
        if (null != maxEligibleTenure && month > maxEligibleTenure) {
            return true;
        }
        if (null != cardTypeEnum && CardTypeEnum.CREDIT.name().equals(cardTypeEnum.name()) && null != ccMaxTenure &&
                month > ccMaxTenure) {
            return true;
        }
        if (null != cardTypeEnum && CardTypeEnum.DEBIT.name().equals(cardTypeEnum.name()) && null != dcMaxTenure &&
                month > dcMaxTenure) {
            return true;
        }
        if (null != cardTypeEnum && CardTypeEnum.CARDLESS.name().equals(cardTypeEnum.name()) && null != dcMaxTenure &&
                month > dcMaxTenure) {
            return true;
        }

        return null != merchantParams && null != merchantParams.getExclusionTenures() &&
                Arrays.stream(merchantParams.getExclusionTenures().split(","))
                        .anyMatch(s1 -> s1.equals(month.toString()));
    }

    private boolean hasMerchantRestricted(Params merchantParams, CardTypeEnum cardTypeEnum, BankEnum bankEnum,
                                          CardTypeEnum cardType, String mccCode, Boolean downpaymentEnabled) {
        if ("5571".equals(mccCode)) {
            if (BankEnum.HDFC.getCode().equalsIgnoreCase(bankEnum.getCode()) &&
                    (CardTypeEnum.DEBIT.equals(cardType) || CardTypeEnum.CARDLESS.equals(cardType))) {
                if (null == downpaymentEnabled || !downpaymentEnabled) {
                    return true;
                }
            }
        }
        if (null != merchantParams && null != merchantParams.getExclusionPaymentTypes() &&
                merchantParams.getExclusionPaymentTypes().contains(cardTypeEnum.getCardType())) {
            return true;
        }
        if (CardTypeEnum.DEBIT.equals(cardType)) {
            return null != merchantParams && null != merchantParams.getExclusionDebitBanks() &&
                    merchantParams.getExclusionDebitBanks().contains(bankEnum.getCode());
        } else if (CardTypeEnum.CREDIT.equals(cardType)) {
            return null != merchantParams && null != merchantParams.getExclusionCreditBanks() &&
                    merchantParams.getExclusionCreditBanks().contains(bankEnum.getCode());
        }
        return false;
    }

    private boolean isValidPriceResponseWithDownPaymentOrProcessFee(PriceResponse pr, boolean downPaymentEnabled,
                                                                    boolean onlyProcessingFeeEnabled, Integer tenure,
                                                                    Integer advanceEmiTenure) {
        if (downPaymentEnabled) {
            if (null != advanceEmiTenure && advanceEmiTenure > 0) {
                if (null != pr.getDownPayment() && tenure.equals(pr.getTenure()) &&
                        advanceEmiTenure.equals(pr.getAdvanceEmiTenure())) {
                    return true;
                }
            }
            if (null != pr.getDownPayment() && tenure.equals(pr.getTenure())) {
                return true;
            }
        }
        if (onlyProcessingFeeEnabled) {
            if (null != advanceEmiTenure && advanceEmiTenure > 0) {
                if (null != pr.getDownPayment() && tenure.equals(pr.getTenure()) &&
                        advanceEmiTenure.equals(pr.getAdvanceEmiTenure())) {
                    return true;
                }
            }
            if (null != pr.getProcessingFee() && tenure.equals(pr.getTenure())) {
                return true;
            }
        }
        return false;
    }

    public Float getPgAmount(TransactionResponse tr, PriceResponse priceResponse, Boolean subventGst) {
        Float cFee = null == priceResponse.getConvFee() ? 0.0f : priceResponse.getConvFee();
        Float gstCFee = cFee * .18f;
        if (null != tr.getDownPaymentInfo()) {
            if (null != tr.getDownPaymentInfo().getDpChargeInfo() &&
                    null != tr.getDownPaymentInfo().getDpChargeInfo().getDownPaymentAmount()) {
                return tr.getAmount() - priceResponse.getDiscount() -
                        tr.getDownPaymentInfo().getDpChargeInfo().getDownPaymentAmount();
            }
            //below code is required for initial release later can be removed
            return tr.getAmount() - priceResponse.getDiscount() - tr.getDownPaymentInfo().getAmount();
        }
        float gst = subventGst ? priceResponse.getDiscount() * .18f : 0.0f;
        return tr.getAmount() + cFee + gstCFee - priceResponse.getDiscount() - gst -
                getBnplDiscount(priceResponse.getTenure(), tr.getAmount() + cFee);
    }

    private Float getBnplDiscount(Integer tenure, Float amount) {
        return (null != tenure && tenure > 0) ? 0
                : (amount * CardInterestEnum.BNPL_HDFC_15.getInterestEnum().getInterest()) / 1200;
    }

    public Map<String, List<PriceResponse>> getPgPaymentOptionsOnEligibilityV3(TransactionResponse tr) {
        Map<String, List<PriceResponse>> cardOffers = new HashMap<>();
        String brandId = "";
        String productId = "";
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = schemeConfigBO.constructProviderMasterConfigMap(Util.getPartnerCodeByTransactionResponse(tr));
        if (Util.isNotNull(tr.getAdditionInfo()) && Util.isNotNull(tr.getAdditionInfo().getBrandInfo())) {
            brandId = tr.getAdditionInfo().getBrandInfo().getBrandId();
        }
        if (Util.isNotNull(tr.getAdditionInfo()) && Util.isNotNull(tr.getAdditionInfo().getBrandInfo())) {
            productId = tr.getAdditionInfo().getBrandInfo().getBrandProductId();
        }
        List<InterestPerTenureDto> configuredSchemes =
                schemeConfigBO.getSchemeMasterData(Util.getPartnerCodeByTransactionResponse(tr), tr.getMerchantId(),
                        brandId, productId);
        if (!CollectionUtils.isEmpty(tr.getEligibilities())) {
            for (CardTypeEnum cardTypeEnum : CardTypeEnum.values()) {
                for (BankEnum bankEnum : BankEnum.values()) {
                    if (CardTypeEnum.CREDIT.getCardType().equals(cardTypeEnum.getCardType()) &&
                            !tr.getEligibilities().contains("CC")) {
                        continue;
                    }
                    if (!CardTypeEnum.CREDIT.getCardType().equals(cardTypeEnum.getCardType())) {
                        if (CardTypeEnum.NTB.getCardType().equals(cardTypeEnum.getCardType())) {
                            if (!tr.getEligibilities().contains(bankEnum.getCode()) || !tr.getNtbEligibilities()
                                    .stream()
                                    .map(EligibilityResponse::getBankCode)
                                    .collect(Collectors.toList())
                                    .contains(bankEnum.getCode())) {
                                continue;
                            }
                        } else if (!tr.getEligibilities().contains(bankEnum.getCode())) {
                            continue;
                        }
                    }
                    if (!hasMerchantRestricted(tr.getMerchantParams(), cardTypeEnum, bankEnum, cardTypeEnum,
                            null != tr.getAdditionInfo() ? tr.getAdditionInfo().getMccCode() : "",
                            tr.getDownPaymentEnabled())) {
                        List<PriceResponse> priceResponses = new ArrayList<>();
                        List<InterestPerTenureDto> emiInterestPerTenureList;
                        // List Of All Applicable EMI Options with BankInterests and Emi Tenure from Schemes
                        emiInterestPerTenureList = getEmiInterestPerTenureListV2(tr, cardTypeEnum, bankEnum,
                                providerMasterConfigInfoMap, configuredSchemes, brandId);
                        if (!CollectionUtils.isEmpty(emiInterestPerTenureList)) {
                            for (InterestPerTenureDto interestPerTenureDto : emiInterestPerTenureList) {
                                //Find emi offers for all the configured tenures
                                List<PriceResponse> emiOffers = null;
                                try {
                                    emiOffers = emiOffersByBankInterestDto(tr, interestPerTenureDto, providerMasterConfigInfoMap, configuredSchemes, brandId);
                                } catch (Exception e) {
                                    LOGGER.error(
                                            "Exception occurred as :{} while finding emi option for card type: {}, " +
                                                    "bank " + "code: {}, tenure: {}", e, cardTypeEnum.getCardType(),
                                            bankEnum.getCode(), interestPerTenureDto.getTenure());
                                }
                                if (!CollectionUtils.isEmpty(emiOffers)) {
                                    for (PriceResponse emiOffer : emiOffers) {
                                        if (isStandardBankInterestApplicable(emiOffer, interestPerTenureDto)) {
                                            InterestPerTenureDto standardInterestPerTenure =
                                                    schemeConfigBO.getStandardInterestPerTenure(cardTypeEnum.getCardType(),
                                                            bankEnum.getCode(), emiOffer.getTenure(),
                                                            providerMasterConfigInfoMap);
                                            List<PriceResponse> standardEmiOffersByStandardIrr =
                                                    emiOffersByBankInterestDto(tr, standardInterestPerTenure, providerMasterConfigInfoMap, configuredSchemes, brandId);
                                            if (Objects.nonNull(standardEmiOffersByStandardIrr)) {
                                                priceResponses.addAll(standardEmiOffersByStandardIrr);
                                            }
                                        } else {
                                            priceResponses.add(emiOffer);
                                        }
                                    }
                                }
                            }
                        }
                        if (!CollectionUtils.isEmpty(priceResponses)) {
                            cardOffers.put(getCardOfferKey(cardTypeEnum, bankEnum), priceResponses);
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(cardOffers)) {
            throw new FreewayCustomException(TransactionCode.FAILED_183);
        }
        return cardOffers;
    }

    private Boolean isStandardBankInterestApplicable(PriceResponse emiOffer,
                                                     InterestPerTenureDto interestPerTenureDto) {
        return EMIOfferType.STANDARD.equals(Util.getTypeOfEMIOffer(emiOffer)) &&
                !BankInterestTypeEnum.STANDARD.equals(interestPerTenureDto.getBankInterestTypeEnum()) &&
                (StringUtils.isEmpty(interestPerTenureDto.getApplicabilityType()) ||
                        ApplicabilityTypeEnum.OFFER.name().equals(interestPerTenureDto.getApplicabilityType()));
    }

    // Version 2 Payment Option BO will below features
    // 1. It will calculate EMI Offers based on configured Bank Interest rates Either on brand or merchant Along with
    // standard IRR.
    // 2. It will return Multi Offer cards with respect to CARD_TYPE, BANK, TENURE with advance EMI Options
    public Map<String, List<PriceResponse>> getPgPaymentOptionsV2(TransactionResponse tr) {
        Map<String, List<PriceResponse>> cardOffers = new HashMap<>();
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = schemeConfigBO.constructProviderMasterConfigMap(Util.getPartnerCodeByTransactionResponse(tr));
        String brandId = Util.isNotNull(tr.getAdditionInfo()) && Util.isNotNull(tr.getAdditionInfo().getBrandInfo()) &&
                BooleanUtils.isTrue(tr.getIsBrandSubventionModel()) ? tr.getAdditionInfo().getBrandInfo().getBrandId()
                : null;

        String productId = "";
        if (Util.isNotNull(tr.getAdditionInfo()) && Util.isNotNull(tr.getAdditionInfo().getBrandInfo())) {
            productId = tr.getAdditionInfo().getBrandInfo().getBrandProductId();
        }
        List<InterestPerTenureDto> configuredSchemes =
                schemeConfigBO.getSchemeMasterData(Util.getPartnerCodeByTransactionResponse(tr), tr.getMerchantId(),
                        brandId, productId);

        for (CardTypeEnum cardTypeEnum : CardTypeEnum.values()) {
            for (BankEnum bankEnum : BankEnum.values()) {
                if (!hasMerchantRestricted(tr.getMerchantParams(), cardTypeEnum, bankEnum, cardTypeEnum,
                        null != tr.getAdditionInfo() ? tr.getAdditionInfo().getMccCode() : "",
                        tr.getDownPaymentEnabled())) {
                    List<InterestPerTenureDto> emiInterestPerTenureList =
                            getEmiInterestPerTenureListV2(tr, cardTypeEnum, bankEnum,
                                    providerMasterConfigInfoMap, configuredSchemes,
                                    brandId);


                    List<PriceResponse> priceResponseList = new ArrayList<>();

                    if (!CollectionUtils.isEmpty(emiInterestPerTenureList)) {
                        for (InterestPerTenureDto interestPerTenureDto : emiInterestPerTenureList) {
                            //Find emi offers for all the configured tenures
                            List<PriceResponse> emiOffers = null;
                            try {
                                emiOffers = emiOffersByBankInterestDto(tr, interestPerTenureDto, providerMasterConfigInfoMap, configuredSchemes, brandId);
                            } catch (Exception e) {
                                LOGGER.error(
                                        "Exception occurred as :{} while finding emi option for card type: {}, bank " +
                                                "code: {}, tenure: {}", e, cardTypeEnum.getCardType(),
                                        bankEnum.getCode(), interestPerTenureDto.getTenure());
                            }
                            if (!CollectionUtils.isEmpty(emiOffers)) {
                                for (PriceResponse emiOffer : emiOffers) {
                                    if (isStandardBankInterestApplicable(emiOffer, interestPerTenureDto)) {
                                        InterestPerTenureDto standardInterestPerTenure =
                                                schemeConfigBO.getStandardInterestPerTenure(cardTypeEnum.getCardType(),
                                                        bankEnum.getCode(), emiOffer.getTenure(),
                                                        providerMasterConfigInfoMap);
                                        List<PriceResponse> standardEmiOffersByStandardIrr =
                                                emiOffersByBankInterestDto(tr, standardInterestPerTenure, providerMasterConfigInfoMap, configuredSchemes, brandId);
                                        PriceResponse standardEmiOfferByStandardIrr =
                                                filterForSpecificEmiOffer(emiOffer, standardEmiOffersByStandardIrr);
                                        if (Objects.nonNull(standardEmiOfferByStandardIrr)) {
                                            priceResponseList.add(standardEmiOfferByStandardIrr);
                                        }
                                    } else {
                                        priceResponseList.add(emiOffer);
                                    }
                                }
                            }
                        }
                    }
                    if (!CollectionUtils.isEmpty(priceResponseList)) {
                        cardOffers.put(getCardOfferKey(cardTypeEnum, bankEnum), priceResponseList);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(cardOffers)) {
            throw new FreewayException("No Offers Found");
        }
        return cardOffers;
    }

    private PriceResponse filterForSpecificEmiOffer(PriceResponse emiOffer,
                                                    List<PriceResponse> standardEmiOffersByStandardIrr) {
        if (!CollectionUtils.isEmpty(standardEmiOffersByStandardIrr)) {
            return standardEmiOffersByStandardIrr.stream()
                    .filter(priceResponse -> ((StringUtils.isEmpty(emiOffer.getDownpaymentType()) &&
                            StringUtils.isEmpty(priceResponse.getDownpaymentType())) ||
                            emiOffer.getDownpaymentType().equalsIgnoreCase(priceResponse.getDownpaymentType())))
                    .filter(priceResponse -> getTenure(emiOffer).equals(getTenure(priceResponse)))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private Integer getTenure(PriceResponse priceResponse) {
        if (Objects.nonNull(priceResponse.getTenure()) && Objects.nonNull(priceResponse.getAdvanceEmiTenure())) {
            return priceResponse.getTenure() - priceResponse.getAdvanceEmiTenure();
        }
        return priceResponse.getTenure();
    }

    public List<InterestPerTenureDto> getEmiInterestPerTenureListV2(TransactionResponse tr, CardTypeEnum cardTypeEnum,
                                                                    BankEnum bankEnum,
                                                                    Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap,
                                                                    List<InterestPerTenureDto> configuredSchemes,
                                                                    String brandId) {
        cardTypeEnum = CardTypeEnum.valueOf(Util.getEffectiveCardType(bankEnum.getCode(), cardTypeEnum.getCardType()));
        List<InterestPerTenureDto> configuredInterestPerTenureDtoList = null;
        List<InterestPerTenureDto> standardInterestPerTenureDtoList =
                schemeConfigBO.getStandardInterestPerTenureDtoList(cardTypeEnum.getCardType(), bankEnum.getCode(),
                        providerMasterConfigInfoMap);

        if (!CollectionUtils.isEmpty(configuredSchemes)) {
            configuredInterestPerTenureDtoList =
                    Util.getInterestPerTenureDtoList(configuredSchemes, cardTypeEnum, bankEnum);

        }

        // Combining Two Lists to get ALL Possible  EMI Options for the transaction configured along with the
        // standard
        // This list will contain all the configured EMI rates along with standard rates if are not configured
        return getCombineListEmiInterestPerTenures(configuredInterestPerTenureDtoList,
                standardInterestPerTenureDtoList);

    }

    public InterestPerTenureDto getEmiInterestPerTenure(TransactionResponse tr, CardTypeEnum cardTypeEnum,
                                                        BankEnum bankEnum, Integer tenure, Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap,
                                                        List<InterestPerTenureDto> configuredSchemes,
                                                        String brandId) {
        List<InterestPerTenureDto> interestPerTenureDtoList = getEmiInterestPerTenureListV2(tr, cardTypeEnum, bankEnum,
                providerMasterConfigInfoMap, configuredSchemes, brandId);
        return interestPerTenureDtoList.stream()
                .filter(interestPerTenureDto -> interestPerTenureDto.getTenure().equals(tenure))
                .findFirst()
                .orElse(null);
    }

    /**
     * Left Array = configuredInterestPerTenureDtoList
     * Right Array = standardInterestPerTenureDtoList
     * <p>
     * Result Array = configuredInterestPerTenureDtoList + NonCommonEle(configuredInterestPerTenureDtoList,
     * standardInterestPerTenureDtoList)
     */
    private List<InterestPerTenureDto> getCombineListEmiInterestPerTenures(
            List<InterestPerTenureDto> configuredInterestPerTenureDtoList,
            List<InterestPerTenureDto> standardInterestPerTenureDtoList) {

        if (CollectionUtils.isEmpty(configuredInterestPerTenureDtoList)) {
            return standardInterestPerTenureDtoList;
        }
        if (CollectionUtils.isEmpty(standardInterestPerTenureDtoList)) {
            return configuredInterestPerTenureDtoList;
        }
        List<InterestPerTenureDto> interestPerTenureDtoList = new ArrayList<>(configuredInterestPerTenureDtoList);
        for (InterestPerTenureDto standardInterestPerTenure : standardInterestPerTenureDtoList) {
            boolean emiTenureIsPresent = false;
            for (InterestPerTenureDto configuredInterestPerTenure : configuredInterestPerTenureDtoList) {
                if (standardInterestPerTenure.getTenure().equals(configuredInterestPerTenure.getTenure())) {
                    emiTenureIsPresent = true;
                    break;
                }
            }
            if (!emiTenureIsPresent) {
                interestPerTenureDtoList.add(standardInterestPerTenure);
            }
        }
        interestPerTenureDtoList.sort(Comparator.comparing(InterestPerTenureDto::getTenure));
        return interestPerTenureDtoList;
    }


    public boolean isEmiTenureEnabledForTxn(TransactionResponse transactionResponse,
                                            InterestPerTenureDto interestPerTenureDto) {
        BankEnum bankEnum = interestPerTenureDto.getBankEnum();
        CardTypeEnum cardTypeEnum = interestPerTenureDto.getCardType();
        Integer emiTenure = interestPerTenureDto.getTenure();
        Integer maxEligibleTenure =
                getBanksMaxEligibilityTenure(transactionResponse.getAdditionInfo(), interestPerTenureDto.getBankEnum());
        if (!hasTenuresRestricted(transactionResponse.getMerchantParams(), transactionResponse.getMaxTenure(),
                transactionResponse.getAdditionInfo().getDcMaxTenure(),
                transactionResponse.getAdditionInfo().getCcMaxTenure(), cardTypeEnum, emiTenure, maxEligibleTenure)) {
            if (showIciciCardless18MonthsTenure(transactionResponse.getMerchantParams().getShowIciciCardless18Months(),
                    emiTenure, bankEnum)) {
                if (showHdfcDcEmi24MonthsTenure(transactionResponse.getMerchantParams().getShowHdfcDcEmi24Months(),
                        emiTenure, bankEnum, cardTypeEnum)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<PriceResponse> emiOffersByBankInterestDto(TransactionResponse tr,
                                                          InterestPerTenureDto interestPerTenureDto,
                                                          Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap,
                                                          List<InterestPerTenureDto> configuredSchemes, String brandId) {
        if (Util.isNull(interestPerTenureDto)) {
            return null;
        }
        List<PriceResponse> priceResponseList = new ArrayList<>();
        BankEnum bankEnum = interestPerTenureDto.getBankEnum();
        CardTypeEnum cardTypeEnum = interestPerTenureDto.getCardType();
        float bankInterestRate = interestPerTenureDto.getIrr().floatValue();
        Float bankInterestRateByBrand =
                Util.isNotNull(interestPerTenureDto.getBrandIrr()) ? interestPerTenureDto.getBrandIrr().floatValue()
                        : null;
        Integer emiTenure = interestPerTenureDto.getTenure();
        brandId = Util.isNotNull(tr.getAdditionInfo()) && Util.isNotNull(tr.getAdditionInfo().getBrandInfo()) &&
                BooleanUtils.isTrue(tr.getIsBrandSubventionModel()) ? tr.getAdditionInfo().getBrandInfo().getBrandId()
                : null;
        CardTypeEnum effectiveCardTypeEnum =
                CardTypeEnum.valueOf(Util.getEffectiveCardType(bankEnum.getCode(), cardTypeEnum.getCardType()));
        // Below Method will be used to valid all the tenure related conditions
        if (isEmiTenureEnabledForTxn(tr, interestPerTenureDto)) {
            String effectiveCardType = Util.getEffectiveCardType(bankEnum.getCode(), cardTypeEnum.getCardType());
            Input input = Util.getInput(tr, effectiveCardType, bankEnum, emiTenure, null);

            // below list will contain all the configured offers enabled for this txn
            List<Output> subventionOffers = null;

            if (Util.isCashbackConfiguredAsInstantDiscount(tr)) {
                subventionOffers = cashbackBO.calculateOffers(input);
            } else {
                subventionOffers = cashbackBO.isEmiCashbackApplicable(brandId, input.getBankCode(), tr.getIsBrandSubventionModel())
                        ? cashbackBO.calculateOffers(input) : null;
                boolean brandCashbackApplied = !CollectionUtils.isEmpty(subventionOffers) && subventionOffers.stream()
                        .anyMatch(output -> output.getCashback() != null && output.getCashback() > 0f);
                if (!brandCashbackApplied) {
                    subventionOffers = offerBrmsBO.calculateOffers(input);
                }
            }

            for (Output output : subventionOffers) {
                if (Util.isNull(output)) {
                    continue;
                }
                Float discountRate;
                Float cashbackRate = 0f;
                Integer effectiveTenure;
                OfferDetails additionalCashback = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
                if (Util.isCashbackConfiguredAsInstantDiscount(tr)) {
                    discountRate = null != output.getCashback() ? output.getCashback() : 0f;
                    effectiveTenure = output.getEffectiveTenure();
                } else {
                    discountRate = null != output.getDiscount() ? output.getDiscount() : 0f;
                    cashbackRate = null != output.getCashback() ? output.getCashback() : 0f;
                    effectiveTenure = output.getEffectiveTenure();
                    input = Util.getInput(tr, effectiveCardType, bankEnum, emiTenure, effectiveTenure);
                    additionalCashback = null != tr.getIsBrandSubventionModel() && tr.getIsBrandSubventionModel()
                            ? cashbackBO.calculateAdditionalCashback(input) : additionalCashback;
                }
                DecimalFormat df = new DecimalFormat("0.00");
                Float cFee = Float.valueOf(df.format(convenienceFeeBO.calculate(tr.getAmount(), input)));
                float convenienceFlatFee;
                if (tr.getIsConvFee() && cFee > 0.0f) {
                    if (tr.getAmount() > Float.parseFloat(cutoffTransactionAmount))
                        convenienceFlatFee = Float.parseFloat(maxConvenienceFlatFee);
                    else
                        convenienceFlatFee = Float.parseFloat(minConvenienceFlatFee);
                    cFee = Math.max(cFee, convenienceFlatFee);
                }
                Instant expectedCashbackDate = null != tr.getIsBrandSubventionModel() && tr.getIsBrandSubventionModel()
                        ? cashbackBO.getExpectedCashbackDate(
                        null != tr.getAdditionInfo() ? tr.getAdditionInfo().getBrandInfo() : null) : null;

                float discount = Float.parseFloat(df.format(tr.getAmount() * discountRate));
                Float cashback = Float.valueOf(df.format(tr.getAmount() * cashbackRate));
                Float effectiveIrr =
                        cashback > 0d && null != bankInterestRateByBrand ? bankInterestRateByBrand : bankInterestRate;
                Boolean subventGst = Util.isSubventGst(cardTypeEnum, tr);
                EligibilityResponse eligibilityResponse =
                        Util.getEligibilityResponseByBank(tr.getNtbEligibilities(), bankEnum);

                // This Configured Processing Fee is to take it upfront in the txn journey
                float processingFeeRate =
                        null != eligibilityResponse && null != eligibilityResponse.getProcessingRate() ?
                                Float.parseFloat(eligibilityResponse.getProcessingRate()) / 100 : 0.0f;
                float maxProcessingFee =
                        null != eligibilityResponse && null != eligibilityResponse.getMaxProcessingFee()
                                ? Util.getFLoat(Float.parseFloat(eligibilityResponse.getMaxProcessingFee())) : 0.0f;
                ProviderParams providerParams =
                        null != eligibilityResponse ? eligibilityResponse.getProviderParams() : null;
                String mccCode = null != tr.getAdditionInfo() ? tr.getAdditionInfo().getMccCode() : null;

                if (Util.isNotNull(effectiveTenure) && effectiveTenure > 0) {
                    if (BooleanUtils.isTrue(tr.getDownPaymentEnabled())) {
                        InterestPerTenureDto effectiveBankInterestDto =
                                getEmiInterestPerTenure(tr, effectiveCardTypeEnum, bankEnum, effectiveTenure, providerMasterConfigInfoMap, configuredSchemes, brandId);
                        if (Util.isNull(effectiveBankInterestDto)) {
                            LOGGER.error(TransactionCode.FAILED_183.getDashboardStatusMsg() + " for transaction id: " +
                                    tr.getTxnId());
                            throw new FreewayCustomException(TransactionCode.FAILED_184);
                        }

                        // in instant cashback flow, cashback is calculated on (amount - downpayment) so adding this
                        // check to show correct values in merchant app.
                        if (tr.getIsInstantCashbackEnabled() != null && tr.getIsInstantCashbackEnabled()) {
                            int advanceEmiTenure = interestPerTenureDto.getTenure() - effectiveTenure;
                            float downPayment = (tr.getAmount() / interestPerTenureDto.getTenure()) * advanceEmiTenure;
                            cashback = Float.parseFloat(df.format((tr.getAmount() - downPayment) * cashbackRate));

                        }

                        PriceResponse response = fixDownpaymentFlowByInterestPerTenure(interestPerTenureDto.getTenure(),
                                effectiveBankInterestDto.getIrr(), effectiveBankInterestDto.getBrandIrr(),
                                interestPerTenureDto.getCardInterestId(), cFee, cashback, expectedCashbackDate,
                                effectiveCardType, discount, tr.getAmount(), subventGst, processingFeeRate,
                                maxProcessingFee, Util.isSubventProcessingFee(tr), bankEnum.getCode(), providerParams,
                                effectiveTenure);
                        response.setAdditionalCashback(additionalCashback.getOfferAmount());
                        response.setBankProcessingIncGst(
                                Util.getBankProcessingFeeIncGst(response.getPgAmount(), effectiveBankInterestDto));
                        response.setOrderAmount(tr.getOrderAmount());
                        populateAdditionalDetails(response, interestPerTenureDto, providerMasterConfigInfoMap);
                        if (isEMIOfferEnabled(tr, response)) {
                            priceResponseList.add(response);
                        }
                    }
                    continue;
                }
                PriceResponse response = getPgPriceResponse(cardTypeEnum.getCardType(), bankEnum.getCode(),
                        interestPerTenureDto.getTenure(), discount, tr.getAmount(), tr.getSplitAmount(), cFee, cashback,
                        expectedCashbackDate, subventGst, processingFeeRate, maxProcessingFee,
                        interestPerTenureDto.getCardInterestId(), effectiveIrr, 0, Util.isSubventProcessingFee(tr),
                        providerParams, interestPerTenureDto.getCalculationType());
                response.setAdditionalCashback(additionalCashback.getOfferAmount());
                response.setBankProcessingIncGst(
                        Util.getBankProcessingFeeIncGst(response.getPgAmount(), interestPerTenureDto));
                response.setOrderAmount(tr.getOrderAmount());
                populateAdditionalDetails(response, interestPerTenureDto, providerMasterConfigInfoMap);
                if (isEmiTenureRestrictedByPaymentProvider(tr, bankEnum, cardTypeEnum, effectiveCardType,
                        effectiveTenure, response)) {
                    continue;
                }
                if (isEMIOfferEnabled(tr, response)) {
                    priceResponseList.add(response);
                }
                if (response.getBankCharges() > (response.getDiscount() + cashback) &&
                        null != tr.getDownPaymentEnabled() && tr.getDownPaymentEnabled() &&
                        (null == tr.getSplitAmount() || tr.getSplitAmount() <= 0) &&
                        (tr.getIsInstantCashbackEnabled() == null || !tr.getIsInstantCashbackEnabled())) {
                    PriceResponse dpr = null;
                    DownPaymentConfigDto downPaymentConfig = tr.getDownPaymentConfig();
                    if (Util.isNotNull(downPaymentConfig) &&
                            !CollectionUtils.isEmpty(downPaymentConfig.getDownPaymentRules())) {
                        List<DownPaymentRulesDto> dpRules = downPaymentConfig.getDownPaymentRules();
                        for (DownPaymentRulesDto dpRule : dpRules) {
                            if (paymentConstants.ALL.equalsIgnoreCase(dpRule.getCardType()) &&
                                    paymentConstants.ALL.equalsIgnoreCase(dpRule.getBankCode())) {
                                dpr = Util.noCostEmiWithdownPayment(cFee, cashback, expectedCashbackDate,
                                        interestPerTenureDto.getTenure(), effectiveIrr,
                                        interestPerTenureDto.getCardType().getCardType(),
                                        interestPerTenureDto.getCardInterestId(), response.getDiscount(),
                                        tr.getAmount() - response.getDiscount(), subventGst, processingFeeRate,
                                        maxProcessingFee, 0, Util.isSubventProcessingFee(tr), discount, providerParams);
                            } else {
                                if (interestPerTenureDto.getCardType()
                                        .getCardType()
                                        .equalsIgnoreCase(dpRule.getCardType()) &&
                                        bankEnum.getCode().equalsIgnoreCase(dpRule.getBankCode())) {
                                    dpr = Util.noCostEmiWithdownPayment(cFee, cashback, expectedCashbackDate,
                                            interestPerTenureDto.getTenure(), effectiveIrr, dpRule.getCardType(),
                                            interestPerTenureDto.getCardInterestId(), response.getDiscount(),
                                            tr.getAmount() - response.getDiscount(), subventGst, processingFeeRate,
                                            maxProcessingFee, 0, Util.isSubventProcessingFee(tr), discount,
                                            providerParams);
                                }
                            }
                        }
                    } else {
                        dpr = Util.noCostEmiWithdownPayment(cFee, cashback, expectedCashbackDate,
                                interestPerTenureDto.getTenure(), effectiveIrr,
                                interestPerTenureDto.getCardType().getCardType(),
                                interestPerTenureDto.getCardInterestId(), response.getDiscount(),
                                tr.getAmount() - response.getDiscount(), subventGst, processingFeeRate,
                                maxProcessingFee, 0, Util.isSubventProcessingFee(tr), discount, providerParams);
                    }
                    if (null != dpr && tr.getAmount() * .4 >= dpr.getDownPayment()) {
                        dpr.setAdditionalCashback(additionalCashback.getOfferAmount());
                        dpr.setBankProcessingIncGst(
                                Util.getBankProcessingFeeIncGst(dpr.getPgAmount(), interestPerTenureDto));
                        dpr.setBankProcessingFee(
                                Util.getBankProcessingFee(dpr.getPgAmount(), interestPerTenureDto));
                        dpr.setOrderAmount(tr.getOrderAmount());
                        populateAdditionalDetails(response, interestPerTenureDto, providerMasterConfigInfoMap);
                        if (isEMIOfferEnabled(tr, dpr)) {
                            priceResponseList.add(dpr);
                        }
                    }
                }
            }
        }
        return priceResponseList;
    }

    private boolean isEMIOfferEnabled(TransactionResponse transactionResponse, PriceResponse priceResponse) {
        boolean isEMIOfferEnabled = true;
        if (Util.isNotNull(transactionResponse) && Util.isNotNull(transactionResponse.getAdditionInfo()) &&
                Util.isNotNull(transactionResponse.getAdditionInfo().getBrandInfo()) &&
                Util.isNotNull(transactionResponse.getAdditionInfo().getBrandInfo().getEmiOfferType())) {
            EMIOfferType emiOfferType = transactionResponse.getAdditionInfo().getBrandInfo().getEmiOfferType();
            Float subvention = Util.isNotNull(priceResponse.getDiscount()) && priceResponse.getDiscount() > 0f
                    ? priceResponse.getDiscount() : priceResponse.getCashback();
            Float interest = priceResponse.getBankCharges();
            switch (emiOfferType) {
                case NOCOST:
                    isEMIOfferEnabled = Util.isNotCostEMI(interest, subvention);
                    break;
                case STANDARD:
                    isEMIOfferEnabled = Util.isStandardCostEMI(interest, subvention);
                    break;
                case LOWCOST:
                    isEMIOfferEnabled = Util.isLowCostEMI(interest, subvention);
                    break;
                case SUBVENTED:
                    isEMIOfferEnabled =
                            Util.isNotCostEMI(interest, subvention) || Util.isLowCostEMI(interest, subvention);
                    break;
                default:
                    isEMIOfferEnabled = true;
                    break;
            }
        }
        return isEMIOfferEnabled;
    }

    public String getCardOfferKey(CardTypeEnum cardTypeEnum, BankEnum bankEnum) {
        switch (cardTypeEnum.getCardType()) {
            case paymentConstants.DEBIT:
            case paymentConstants.CARDLESS:
                return "D_" + bankEnum.getCode();
            case paymentConstants.BNPL:
                return bankEnum.getCode();
            case paymentConstants.NTB:
                return "NTB_" + bankEnum.getCode();
            default:
                return bankEnum.getCode();
        }
    }

    private void populateAdditionalDetails(PriceResponse response, InterestPerTenureDto interestPerTenureDto,
                                           Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        InterestPerTenureDto standardInterestPerTenureDto =
                schemeConfigBO.getStandardInterestPerTenure(interestPerTenureDto.getCardType().getCardType(),
                        interestPerTenureDto.getBankEnum().getCode(), interestPerTenureDto.getTenure(),
                        providerMasterConfigInfoMap);

        if (Objects.nonNull(standardInterestPerTenureDto)) {
            response.setMinTxnVal(Objects.nonNull(standardInterestPerTenureDto.getMinAmount())
                    ? standardInterestPerTenureDto.getMinAmount().floatValue() : null);
            response.setMaxTxnVal(Objects.nonNull(standardInterestPerTenureDto.getMaxAmount())
                    ? standardInterestPerTenureDto.getMaxAmount().floatValue() : null);
        }
    }

}
