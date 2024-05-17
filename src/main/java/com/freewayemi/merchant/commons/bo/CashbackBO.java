package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.bo.brms.OfferBrmsBO;
import com.freewayemi.merchant.commons.bo.brms.Output;
import com.freewayemi.merchant.commons.dto.BrandInfo;
import com.freewayemi.merchant.commons.dto.OfferDetails;
import com.freewayemi.merchant.commons.dto.OfferDetailsResponse;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantInstantDiscountConfigResp;
import org.apache.commons.lang.BooleanUtils;
import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.min;

@Component
public class CashbackBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CashbackBO.class);
    private final PaymentServiceBO paymentServiceBO;
    private final OfferBrmsBO offerBrmsBO;

    public CashbackBO(PaymentServiceBO paymentServiceBO, OfferBrmsBO offerBrmsBO) {
        this.paymentServiceBO = paymentServiceBO;
        this.offerBrmsBO = offerBrmsBO;
    }

    public boolean isEmiCashbackApplicable(String brandId, String bankCode, Boolean isBrandSubventionModel) {
        List<String> yesBankNoCashbackBrands =
                Arrays.asList(new String[]{"613066ce93820955fdac0f00", "6138a84b6bad2c458cf99a09"});
        if (isBrandSubventionModel == null || !isBrandSubventionModel ||
                ("YESB".equals(bankCode) && yesBankNoCashbackBrands.contains(brandId))) {
            return false;
        }
        return true;
    }

    public List<Output> calculateOffers(Input input) {
        List<Output> outputs = new ArrayList<>();
        // Single default offer
        outputs.add(calculate(input));
        outputs.addAll(findEffectiveTenureOffers(input));
        return outputs;
    }

    private List<Output> findEffectiveTenureOffers(Input input) {
        List<Output> outputs = new ArrayList<>();
        Instant currentDate = Instant.now();

        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return outputs;
        }
        if (null == input.getBrandSubventions() || 0 == input.getBrandSubventions().size()) {
            return outputs;
        }

        List<OfferResponse> effectiveTenureOffers = input.getBrandSubventions()
                .stream()
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() &&
                        offerResponse.getEffectiveTenure() > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(effectiveTenureOffers)) {
            return outputs;
        }

        return effectiveTenureOffers.stream()
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() &&
                        offerResponse.getEffectiveTenure() > 0)
                .filter(offerResponse -> (null == offerResponse.getType() ||
                        !offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback")))
                .filter(offerResponse -> input.getBankCode().equals(offerResponse.getBankCode()) &&
                        input.getCardType().equals(offerResponse.getCardType()) &&
                        input.getTenure().equals(offerResponse.getTenure()) &&
                        isValidTxnAmount(input.getTxnAmount(), offerResponse.getMinAmount()))
                .filter(offerResponse -> Util.isUnderValidTimePeriod(currentDate, offerResponse.getValidFrom(),
                        offerResponse.getValidTo()))
                .filter(offerResponse -> isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                        offerResponse.getProductIds()))
                .map(offerResponse -> new Output(0f, offerResponse.getSubvention() / 100,
                        offerResponse.getEffectiveTenure(), offerResponse.getOfflineAdvanceEmiTenure()))
                .collect(Collectors.toList());
    }


    public Output calculate(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return new Output(0f, 0f, 0, 0);
        }

        if (null == input.getBrandSubventions() || 0 == input.getBrandSubventions().size()) {
            return new Output(0f, 0f, 0, 0);
        }
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if ((null == offerResponse.getType() ||
                    !offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback")) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    (offerResponse.getMinAmount() == null || offerResponse.getMinAmount() <= input.getTxnAmount()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure())) {
                float subvention = null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() ?
                        currentDate.isAfter(offerResponse.getValidFrom()) &&
                                currentDate.isBefore(offerResponse.getValidTo()) ? offerResponse.getSubvention() : 0.0f
                        : offerResponse.getSubvention();
                //return subvention / 100;
                return new Output(0f, subvention / 100, 0, 0);
            }
        }

        List<OfferResponse> out = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(),
                        null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure()
                                : offer.getTenure(), null != offer.getValidFrom() && null != offer.getValidTo() ?
                        (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        null == offer.getCardType() ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), null == offer.getBankCode() ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null,
                        null, offer.getMaxBankShare(), offer.getMaxBrandShare(), offer.getVelocity(),
                        offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> null == offer.getType() ||
                        !offer.getType().equalsIgnoreCase("brandBankAdditionalCashback"))
                .filter(offer -> (null == offer.getCardType() && null == input.getCardType()) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (null == offer.getBankCode() && null == input.getBankCode()) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure()) ||
                        offer.getTenure().equals(input.getTenure()))
                .filter(offerResponse -> (offerResponse.getMinAmount() == null ||
                        offerResponse.getMinAmount() <= input.getTxnAmount()))
                .filter(offerResponse -> isValidEffectiveEmiTenure(input.getEffectiveTenure(),
                        offerResponse.getEffectiveTenure()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return new Output(0f, 0f, 0, 0);
        }

        return new Output(0f, out.get(0).getSubvention() / 100, 0, out.get(0).getOfflineAdvanceEmiTenure());
    }


    @Obsolete
    private boolean isProductCriteriaOld(String productId, String brandProductId, List<String> brandProductIds) {
        return (StringUtils.isEmpty(productId) ||
                (StringUtils.hasText(productId) && StringUtils.hasText(brandProductId) &&
                        "any".equals(brandProductId)) ||
                (StringUtils.hasText(productId) && null != brandProductIds && brandProductIds.size() > 0 &&
                        brandProductIds.contains(productId)));
    }

    public boolean isProductCriteria(String brandProductId, String offerProductId, List<String> brandProductIds) {
        /*
        First If check for brand product
        if user is buying from a product of a brand(realme,daikin, etc.) and product is present in the list then we
        need give the brand subvention
         */
        if (StringUtils.hasText(brandProductId) && null != brandProductIds && brandProductIds.size() > 0) {
            return brandProductIds.contains(brandProductId);
        }
        /*
        Second If check for brand product
        if user is buying from a product of a brand(realme, daikin, etc.) and brand has allowed to give brand
        subvention for all products that shared
        then we put any inside productId of Offer table.
         */
        else if (StringUtils.hasText(brandProductId) && StringUtils.hasText(offerProductId)) {
            return "any".equals(offerProductId);
        }
        /*
        Third If check for product is empty in case of normal transaction
        but brand wanted to give cashback for all transactions then productsId and product will be null in case of
        ttkPrestige.
         */
        else return StringUtils.isEmpty(brandProductId) && StringUtils.isEmpty(offerProductId) &&
                    CollectionUtils.isEmpty(brandProductIds);
    }

    private Integer getScore(OfferResponse offerResponse, Input input) {
        int score = 0;
        if (input.getTenure().equals(offerResponse.getTenure())) score += 1;
        if (input.getCardType().equals(offerResponse.getCardType())) score += 1;
        if (input.getBankCode().equals(offerResponse.getBankCode())) score += 1;
        return score;
    }

    public Instant getExpectedCashbackDate(BrandInfo brandInfo) {
        if (null != brandInfo && null != brandInfo.getPaymentCycle()) {
            return brandInfo.getPaymentCycle() == 90 ? DateUtil.addMonthsAndGetLastDay(3)
                    : brandInfo.getPaymentCycle() == 30 ? DateUtil.getLastDayOfNextMonth()
                            : ZonedDateTime.now().getDayOfMonth() > 15 ? DateUtil.getLastDayOfNextMonth()
                                    : DateUtil.getFifteenthOfNextMonth();
        }
        return DateUtil.addMonthsAndGetLastDay(3);
    }

    public Instant getExpectedPromoCodeCashbackDate() {
        return DateUtil.addMonthsAndGetLastDay(3);
    }

    public Boolean isISGProviderForCredit(String bankCode, String cardType, List<PaymentProviderInfo> providers) {
        if (!cardType.equals("CREDIT")) return true;

        if (!CollectionUtils.isEmpty(providers)) {
            for (PaymentProviderInfo providerInfo : providers) {
                if ((null == providerInfo.getBank() || providerInfo.getBank().getCode().equals(bankCode)) &&
                        (null == providerInfo.getType() || providerInfo.getType().name().equals(cardType)) &&
                        (PaymentProviderEnum.isgpg == providerInfo.getProvider() ||
                                PaymentProviderEnum.easebuzzpg == providerInfo.getProvider() ||
                                PaymentProviderEnum.lyrapg == providerInfo.getProvider() ||
                                PaymentProviderEnum.paymentmockpg == providerInfo.getProvider() ||
                                PaymentProviderEnum.ccavenuepg == providerInfo.getProvider() ||
                                PaymentProviderEnum.ccavenueemipg == providerInfo.getProvider() ||
                                PaymentProviderEnum.cashfreepg == providerInfo.getProvider()) &&
                        !providerInfo.getDisabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public OfferDetails calculateAdditionalCashback(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        if (null != input.getMerchantId()) {
            List<PaymentProviderInfo> providers;
            if (input.getProviders() != null) {
                providers = input.getProviders();
            } else {
                providers = paymentServiceBO.getProvidersInfo(input.getMerchantId());
            }
            if (!isISGProviderForCredit(input.getBankCode(), input.getCardType(), providers)) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
        }
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if (null != offerResponse.getType() &&
                    offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback") &&
                    (offerResponse.getActive() == null || offerResponse.getActive()) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    isStateCriteriaSatisfied(input.getMerchantState(), offerResponse.getApplicableStates(),
                            offerResponse.getExclusionStates()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure()) &&
                    (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                            currentDate.isAfter(offerResponse.getValidFrom()) &&
                            currentDate.isBefore(offerResponse.getValidTo()))) {
                Float offerAmount = 0.0f;
                Float bankShare = 0.0f;
                Float brandShare = 0.0f;
                if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                    return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
                }
                if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                    offerAmount = offerResponse.getMaxOfferAmount();
                    if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0) {
                        bankShare = offerResponse.getBankShareAmt();
                    }
                    if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0) {
                        brandShare = offerResponse.getBrandShareAmt();
                    }
                    LOGGER.info("inside if bankShare: {} & branShare: {}", bankShare, brandShare);
                } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                    offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                            offerResponse.getMaxOfferAmount());
                    if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0 &&
                            offerResponse.getBankPercentShare() != null && offerResponse.getBankPercentShare() > 0) {
                        bankShare = min(offerResponse.getMaxBankShare(),
                                (offerAmount * offerResponse.getBankPercentShare() / 100.0f));
                    } else if (offerResponse.getBankPercentShare() != null && offerResponse.getBankPercentShare() > 0) {
                        bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                    }
                    if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0 &&
                            offerResponse.getBrandPercentShare() != null && offerResponse.getBrandPercentShare() > 0) {
                        brandShare = min(offerResponse.getMaxBrandShare(),
                                (offerAmount * offerResponse.getBrandPercentShare() / 100.0f));
                    } else if (offerResponse.getBrandPercentShare() != null &&
                            offerResponse.getBrandPercentShare() > 0) {
                        brandShare = offerAmount * offerResponse.getBrandPercentShare() / 100.0f;
                    }
                    LOGGER.info("inside else if bankShare: {} & branShare: {}", bankShare, brandShare);
                } else {
                    offerAmount = min(offerResponse.getMaxOfferAmount(),
                            offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                    if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0 &&
                            offerResponse.getBankPercentShare() != null && offerResponse.getBankPercentShare() > 0) {
                        bankShare = min(offerResponse.getMaxBankShare(),
                                (offerAmount * offerResponse.getBankPercentShare() / 100.0f));
                    } else if (offerResponse.getBankPercentShare() != null && offerResponse.getBankPercentShare() > 0) {
                        bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                    }
                    if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0 &&
                            offerResponse.getBrandPercentShare() != null && offerResponse.getBrandPercentShare() > 0) {
                        brandShare = min(offerResponse.getMaxBrandShare(),
                                (offerAmount * offerResponse.getBrandPercentShare() / 100.0f));
                    } else if (offerResponse.getBrandPercentShare() != null &&
                            offerResponse.getBrandPercentShare() > 0) {
                        brandShare = offerAmount * offerResponse.getBrandPercentShare() / 100.0f;
                    }
                    LOGGER.info("inside else bankShare: {} & branShare: {}", bankShare, brandShare);
                }
                if (bankShare > 0 && brandShare <= 0) {
                    return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                            offerResponse);
                }
                LOGGER.info("bankShare: {} & branShare: {}", bankShare, brandShare);
                return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, brandShare, offerResponse);
            }
        }

        List<OfferResponse> out = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(),
                        offer.getTenure() == null || offer.getTenure() == -1 ? input.getTenure() : offer.getTenure(),
                        offer.getSubvention(), offer.getActive(),
                        StringUtils.isEmpty(offer.getCardType()) ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), StringUtils.isEmpty(offer.getBankCode()) ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(),
                        offer.getOfferPercentage(), offer.getBankPercentShare(), offer.getBankShareAmt(),
                        offer.getBrandPercentShare(), offer.getBrandShareAmt(), offer.getMaxBankShare(),
                        offer.getMaxBrandShare(), offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> null != offer.getType() &&
                        offer.getType().equalsIgnoreCase("brandBankAdditionalCashback"))
                .filter(offer -> offer.getActive() == null || offer.getActive())
                .filter(offer -> (StringUtils.isEmpty(offer.getCardType()) && StringUtils.isEmpty(input.getCardType())) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (StringUtils.isEmpty(offer.getBankCode()) && StringUtils.isEmpty(input.getBankCode())) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure() ||
                        offer.getTenure().equals(input.getTenure())))
                .filter(offer -> (null == offer.getProductIds() && null == input.getBrandProductId()) ||
                        offer.getProductIds().contains(input.getBrandProductId()))
                .filter(offer -> isStateCriteriaSatisfied(input.getMerchantState(), offer.getApplicableStates(),
                        offer.getExclusionStates()))
                .filter(offer -> isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()))
                .filter(offer -> (null != offer.getValidFrom() && currentDate.isAfter(offer.getValidFrom()) &&
                        null != offer.getValidTo() && currentDate.isBefore(offer.getValidTo())))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        OfferResponse offerResponse = out.get(0);
        if (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                currentDate.isAfter(offerResponse.getValidFrom()) && currentDate.isBefore(offerResponse.getValidTo())) {
            if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
            Float offerAmount = 0.0f;
            Float bankShare = 0.0f;
            if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                offerAmount = offerResponse.getMaxOfferAmount();
                bankShare = offerResponse.getBankShareAmt();
            } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                        offerResponse.getMaxOfferAmount());
                bankShare = offerAmount * offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            } else {
                offerAmount = min(offerResponse.getMaxOfferAmount(),
                        offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            }
            return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                    offerResponse);
        }
        return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
    }

    public OfferDetails calculateAdditionalInstantDiscount(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        if (null != input.getMerchantId()) {
            List<PaymentProviderInfo> providers;
            if (input.getProviders() != null) {
                providers = input.getProviders();
            } else {
                providers = paymentServiceBO.getProvidersInfo(input.getMerchantId());
            }
            if (!isISGProviderForCredit(input.getBankCode(), input.getCardType(), providers)) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
        }
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if (null != offerResponse.getType() &&
                    offerResponse.getType().equalsIgnoreCase("additionalInstantDiscount") &&
                    (offerResponse.getActive() == null || offerResponse.getActive()) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    isStateCriteriaSatisfied(input.getMerchantState(), offerResponse.getApplicableStates(),
                            offerResponse.getExclusionStates()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure())) {
                Float offerAmount = 0.0f;
                Float bankShare = 0.0f;
                Float brandShare = 0.0f;
                if (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                        currentDate.isAfter(offerResponse.getValidFrom()) &&
                        currentDate.isBefore(offerResponse.getValidTo())) {
                    if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                        return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
                    }
                    if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                        offerAmount = offerResponse.getMaxOfferAmount();
                        if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0) {
                            bankShare = offerResponse.getBankShareAmt();
                        }
                        if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0) {
                            brandShare = offerResponse.getBrandShareAmt();
                        }
                        LOGGER.info("inside if bankShare: {} & branShare: {}", bankShare, brandShare);
                    } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                        offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                                offerResponse.getMaxOfferAmount());
                        if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0 &&
                                offerResponse.getBankPercentShare() != null &&
                                offerResponse.getBankPercentShare() > 0) {
                            bankShare = min(offerResponse.getMaxBankShare(),
                                    (offerAmount * offerResponse.getBankPercentShare() / 100.0f));
                        } else if (offerResponse.getBankPercentShare() != null &&
                                offerResponse.getBankPercentShare() > 0) {
                            bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                        }
                        if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0 &&
                                offerResponse.getBrandPercentShare() != null &&
                                offerResponse.getBrandPercentShare() > 0) {
                            brandShare = min(offerResponse.getMaxBrandShare(),
                                    (offerAmount * offerResponse.getBrandPercentShare() / 100.0f));
                        } else if (offerResponse.getBrandPercentShare() != null &&
                                offerResponse.getBrandPercentShare() > 0) {
                            brandShare = offerAmount * offerResponse.getBrandPercentShare() / 100.0f;
                        }
                        LOGGER.info("inside else if bankShare: {} & branShare: {}", bankShare, brandShare);
                    } else {
                        offerAmount = min(offerResponse.getMaxOfferAmount(),
                                offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                        if (offerResponse.getBankShareAmt() != null && offerResponse.getBankShareAmt() > 0 &&
                                offerResponse.getBankPercentShare() != null &&
                                offerResponse.getBankPercentShare() > 0) {
                            bankShare = min(offerResponse.getMaxBankShare(),
                                    (offerAmount * offerResponse.getBankPercentShare() / 100.0f));
                        } else if (offerResponse.getBankPercentShare() != null &&
                                offerResponse.getBankPercentShare() > 0) {
                            bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                        }
                        if (offerResponse.getBrandShareAmt() != null && offerResponse.getBrandShareAmt() > 0 &&
                                offerResponse.getBrandPercentShare() != null &&
                                offerResponse.getBrandPercentShare() > 0) {
                            brandShare = min(offerResponse.getMaxBrandShare(),
                                    (offerAmount * offerResponse.getBrandPercentShare() / 100.0f));
                        } else if (offerResponse.getBrandPercentShare() != null &&
                                offerResponse.getBrandPercentShare() > 0) {
                            brandShare = offerAmount * offerResponse.getBrandPercentShare() / 100.0f;
                        }
                        LOGGER.info("inside else bankShare: {} & branShare: {}", bankShare, brandShare);
                    }
                    if (bankShare > 0 && brandShare <= 0) {
                        return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                                offerResponse);
                    }
                    LOGGER.info("bankShare: {} & branShare: {}", bankShare, brandShare);
                    return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, brandShare, offerResponse);
                }
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
        }

        List<OfferResponse> out = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(),
                        offer.getTenure() == null || offer.getTenure() == -1 ? input.getTenure() : offer.getTenure(),
                        null != offer.getValidFrom() && null != offer.getValidTo() ?
                                (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                        ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        null == offer.getCardType() ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), null == offer.getBankCode() ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(),
                        offer.getOfferPercentage(), offer.getBankPercentShare(), offer.getBankShareAmt(),
                        offer.getBrandPercentShare(), offer.getBrandShareAmt(), offer.getMaxBankShare(),
                        offer.getMaxBrandShare(), offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> null != offer.getType() &&
                        offer.getType().equalsIgnoreCase("additionalInstantDiscount"))
                .filter(offer -> offer.getActive() == null || offer.getActive())
                .filter(offer -> (null == offer.getCardType() && null == input.getCardType()) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (null == offer.getBankCode() && null == input.getBankCode()) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure() ||
                        offer.getTenure().equals(input.getTenure())))
                .filter(offer -> (null == offer.getProductIds() && null == input.getBrandProductId()) ||
                        offer.getProductIds().contains(input.getBrandProductId()))
                .filter(offer -> isStateCriteriaSatisfied(input.getMerchantState(), offer.getApplicableStates(),
                        offer.getExclusionStates()))
                .filter(offer -> isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        OfferResponse offerResponse = out.get(0);
        if (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                currentDate.isAfter(offerResponse.getValidFrom()) && currentDate.isBefore(offerResponse.getValidTo())) {
            if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
            Float offerAmount = 0.0f;
            Float bankShare = 0.0f;
            if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                offerAmount = offerResponse.getMaxOfferAmount();
                bankShare = offerResponse.getBankShareAmt();
            } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                        offerResponse.getMaxOfferAmount());
                bankShare = offerAmount * offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            } else {
                offerAmount = min(offerResponse.getMaxOfferAmount(),
                        offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            }
            return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                    offerResponse);
        }
        return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
    }

    private boolean isValidTxnAmount(Float txnAmount, Float minimumOfferAmount) {
        if (null != minimumOfferAmount)
            return null != txnAmount && Util.getFLoat(txnAmount) >= Util.getFLoat(minimumOfferAmount);
        else return true;
    }

    private static boolean isValidEffectiveEmiTenure(Integer inputEffectiveTenure, Integer offerEffectiveTenure) {
        if (null != inputEffectiveTenure && inputEffectiveTenure > 0 &&
                inputEffectiveTenure.equals(offerEffectiveTenure)) {
            return true;
        } else if ((null == inputEffectiveTenure || inputEffectiveTenure <= 0) && null == offerEffectiveTenure) {
            return true;
        } else return false;
    }

    public List<OfferDetails> calculateEmiCashbackList(Input input) {
        List<OfferDetails> offerDetailsList = new ArrayList<>();
        offerDetailsList.add(calculateNonEffective(input));
        offerDetailsList.addAll(calculateEffectiveTenureOffers(input));
        return offerDetailsList;
    }

    private List<OfferDetails> calculateEffectiveTenureOffers(Input input) {
        List<OfferDetails> outputs = new ArrayList<>();
        Instant currentDate = Instant.now();

        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return outputs;
        }
        if (null == input.getBrandSubventions() || 0 == input.getBrandSubventions().size()) {
            return outputs;
        }

        List<OfferResponse> effectiveTenureOffers = input.getBrandSubventions()
                .stream()
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() &&
                        offerResponse.getEffectiveTenure() > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(effectiveTenureOffers)) {
            return outputs;
        }

        List<OfferResponse> offerResponses = effectiveTenureOffers.stream()
                .map(offer -> new OfferResponse(offer.getId(),
                        null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure()
                                : offer.getTenure(), null != offer.getValidFrom() && null != offer.getValidTo() ?
                        (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        StringUtils.isEmpty(offer.getCardType()) ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), StringUtils.isEmpty(offer.getBankCode()) ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null,
                        null, null, null, offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() &&
                        offerResponse.getEffectiveTenure() > 0)
                .filter(offerResponse -> (null == offerResponse.getType() ||
                        !offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback")))
                .filter(offerResponse -> input.getBankCode().equals(offerResponse.getBankCode()) &&
                        input.getCardType().equals(offerResponse.getCardType()) &&
                        input.getTenure().equals(offerResponse.getTenure()) &&
                        isValidTxnAmount(input.getTxnAmount(), offerResponse.getMinAmount()))
                .filter(offerResponse -> Util.isUnderValidTimePeriod(currentDate, offerResponse.getValidFrom(),
                        offerResponse.getValidTo()))
                .filter(offerResponse -> isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                        offerResponse.getProductIds()))
                .filter(offerResponse -> isStateCriteriaSatisfied(input.getMerchantState(),
                        offerResponse.getApplicableStates(), offerResponse.getExclusionStates()))
                .collect(Collectors.toList());
        for (OfferResponse offerResponse : offerResponses) {
            OfferDetails offerDetails = new OfferDetails(offerResponse.getId(), 0.0f, 0.0f, 0.0f, offerResponse);
            offerDetails.setOfferRate(offerResponse.getSubvention() / 100);
            outputs.add(offerDetails);
        }
        return outputs;
    }

    private OfferDetails calculateNonEffective(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            return offerDetails;
        }

        if (null == input.getBrandSubventions() || 0 == input.getBrandSubventions().size()) {
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            return offerDetails;
        }
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if ((null == offerResponse.getType() || offerResponse.getType().equalsIgnoreCase("emiCashback")) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    isStateCriteriaSatisfied(input.getMerchantState(), offerResponse.getApplicableStates(),
                            offerResponse.getExclusionStates()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure())) {
                float subvention = null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() ?
                        currentDate.isAfter(offerResponse.getValidFrom()) &&
                                currentDate.isBefore(offerResponse.getValidTo()) ? offerResponse.getSubvention() : 0.0f
                        : offerResponse.getSubvention();

                OfferDetails offerDetails =
                        new OfferDetails(offerResponse.getId(), ((subvention / 100) * input.getTxnAmount()), 0.0f, 0.0f,
                                offerResponse);
                offerDetails.setOfferRate(subvention / 100);
                return offerDetails;
            }
        }
        List<OfferResponse> outList = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(),
                        null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure()
                                : offer.getTenure(), null != offer.getValidFrom() && null != offer.getValidTo() ?
                        (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        StringUtils.isEmpty(offer.getCardType()) ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), StringUtils.isEmpty(offer.getBankCode()) ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null,
                        null, null, null, offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> null == offer.getType() || offer.getType().equalsIgnoreCase("emiCashback"))
                .filter(offer -> (StringUtils.isEmpty(offer.getCardType()) && StringUtils.isEmpty(input.getCardType())) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (StringUtils.isEmpty(offer.getBankCode()) && StringUtils.isEmpty(input.getBankCode())) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> isStateCriteriaSatisfied(input.getMerchantState(), offer.getApplicableStates(),
                        offer.getExclusionStates()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure()) ||
                        offer.getTenure().equals(input.getTenure()))
                .filter(offerResponse -> isValidEffectiveEmiTenure(input.getEffectiveTenure(),
                        offerResponse.getEffectiveTenure()))
                .collect(Collectors.toList());

        if (outList == null || outList.isEmpty()) {
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            return offerDetails;
        }

        OfferDetails offerDetails = new OfferDetails(outList.get(0).getId(), 0.0f, 0.0f, 0.0f, outList.get(0));
        offerDetails.setOfferRate(outList.get(0).getSubvention() / 100);
        return offerDetails;
    }

    public List<OfferDetails> calculateEmiInstantDiscountList(Input input) {
        List<OfferDetails> offerDetailsList = new ArrayList<>();
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
//            return 0.0f;
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            offerDetailsList.add(offerDetails);
            return offerDetailsList;
        }

        if (null == input.getBrandSubventions() || 0 == input.getBrandSubventions().size()) {
//            return 0.0f;
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            offerDetailsList.add(offerDetails);
            return offerDetailsList;
        }
        Instant currentDate = Instant.now();
        Boolean foundAlready = Boolean.FALSE;
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if ((offerResponse.getType() != null && offerResponse.getType().equalsIgnoreCase("emiInstantDiscount")) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    (offerResponse.getMinAmount() == null || offerResponse.getMinAmount() <= input.getTxnAmount()) &&
                    isStateCriteriaSatisfied(input.getMerchantState(), offerResponse.getApplicableStates(),
                            offerResponse.getExclusionStates()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure())) {
                float subvention = null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() ?
                        currentDate.isAfter(offerResponse.getValidFrom()) &&
                                currentDate.isBefore(offerResponse.getValidTo()) ? offerResponse.getSubvention() : 0.0f
                        : offerResponse.getSubvention();
                OfferDetails offerDetails = new OfferDetails(offerResponse.getId(), 0.0f, 0.0f, 0.0f, offerResponse);
                offerDetails.setOfferRate(subvention / 100);
                offerDetailsList.add(offerDetails);
                foundAlready = Boolean.TRUE;
//                return subvention / 100;
            }
        }
        if (foundAlready) {
            return offerDetailsList;
        }

        List<OfferResponse> outList = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(),
                        null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure()
                                : offer.getTenure(), null != offer.getValidFrom() && null != offer.getValidTo() ?
                        (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        null == offer.getCardType() ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), null == offer.getBankCode() ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null,
                        null, null, null, offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> offer.getType() != null && offer.getType().equalsIgnoreCase("emiInstantDiscount"))
                .filter(offer -> (null == offer.getCardType() && null == input.getCardType()) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (null == offer.getBankCode() && null == input.getBankCode()) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> isStateCriteriaSatisfied(input.getMerchantState(), offer.getApplicableStates(),
                        offer.getExclusionStates()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure()) ||
                        offer.getTenure().equals(input.getTenure()))
                .collect(Collectors.toList());

        if (outList == null || outList.isEmpty()) {
//            return 0.0f;
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            offerDetailsList.add(offerDetails);
            return offerDetailsList;
        }

//        return out.get(0).getSubvention() / 100;
        for (OfferResponse out : outList) {
            OfferDetails offerDetails = new OfferDetails(out.getId(), 0.0f, 0.0f, 0.0f, out);
            offerDetails.setOfferRate(out.getSubvention() / 100);
            offerDetailsList.add(offerDetails);
        }
        return offerDetailsList;
    }

    public OfferDetails calculateAdditionalInstantDiscountOld(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) ||
                BooleanUtils.isFalse(input.getIsSubvented())) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        if (null != input.getMerchantId()) {
            List<PaymentProviderInfo> providers = null;
            if (input.getProviders() != null) {
                providers = input.getProviders();
            } else {
                providers = paymentServiceBO.getProvidersInfo(input.getMerchantId());
            }
            if (!isISGProviderForCredit(input.getBankCode(), input.getCardType(), providers)) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
        }
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : input.getBrandSubventions()) {
            if (null != offerResponse.getType() &&
                    offerResponse.getType().equalsIgnoreCase("additionalInstantDiscount") &&
                    (offerResponse.getActive() == null || offerResponse.getActive()) &&
                    input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure()) &&
                    isProductCriteria(input.getBrandProductId(), offerResponse.getProductId(),
                            offerResponse.getProductIds()) &&
                    isValidEffectiveEmiTenure(input.getEffectiveTenure(), offerResponse.getEffectiveTenure())) {
                Float offerAmount = 0.0f;
                Float bankShare = 0.0f;
                if (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                        currentDate.isAfter(offerResponse.getValidFrom()) &&
                        currentDate.isBefore(offerResponse.getValidTo())) {
                    if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                        return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
                    }
                    if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                        offerAmount = offerResponse.getMaxOfferAmount();
                        bankShare = offerResponse.getBankShareAmt();
                    } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                        offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                                offerResponse.getMaxOfferAmount());
                        bankShare = offerAmount * offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                    } else {
                        offerAmount = min(offerResponse.getMaxOfferAmount(),
                                offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                        bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
                    }
                    return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                            offerResponse);
                }
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
        }

        List<OfferResponse> out = input.getBrandSubventions()
                .stream()
                .map(offer -> offer.setScore(getScore(offer, input)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer -> new OfferResponse(offer.getId(), input.getTenure(),
                        null != offer.getValidFrom() && null != offer.getValidTo() ?
                                (currentDate.isAfter(offer.getValidFrom()) && currentDate.isBefore(offer.getValidTo()))
                                        ? offer.getSubvention() : 0.0f : offer.getSubvention(), offer.getActive(),
                        null == offer.getCardType() ? input.getCardType() : offer.getCardType(), offer.getProductId(),
                        offer.getProductIds(), null == offer.getBankCode() ? input.getBankCode() : offer.getBankCode(),
                        offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(),
                        offer.getOfferPercentage(), offer.getBankPercentShare(), offer.getBankShareAmt(),
                        offer.getBrandPercentShare(), offer.getBrandShareAmt(), offer.getMaxBankShare(),
                        offer.getMaxBrandShare(), offer.getVelocity(), offer.getEffectiveTenure(),
                        offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(),
                        offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(),
                        offer.getExclusionStates(), offer.getMaxAmount()).setType(offer.getType()))
                .filter(offer -> null != offer.getType() &&
                        offer.getType().equalsIgnoreCase("additionalInstantDiscount"))
                .filter(offer -> offer.getActive() == null || offer.getActive())
                .filter(offer -> (null == offer.getCardType() && null == input.getCardType()) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (null == offer.getBankCode() && null == input.getBankCode()) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure() ||
                        offer.getTenure().equals(input.getTenure())))
                .filter(offer -> (null == offer.getProductIds() && null == input.getBrandProductId()) ||
                        offer.getProductIds().contains(input.getBrandProductId()))
                .filter(offer -> isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
        }
        OfferResponse offerResponse = out.get(0);
        if (null != offerResponse.getValidFrom() && null != offerResponse.getValidTo() &&
                currentDate.isAfter(offerResponse.getValidFrom()) && currentDate.isBefore(offerResponse.getValidTo())) {
            if (offerResponse.getMinAmount() != null && input.getTxnAmount() < offerResponse.getMinAmount()) {
                return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
            }
            Float offerAmount = 0.0f;
            Float bankShare = 0.0f;
            if (offerResponse.getOfferPercentage() == 0.0f && offerResponse.getMaxOfferAmount() > 0) {
                offerAmount = offerResponse.getMaxOfferAmount();
                bankShare = offerResponse.getBankShareAmt();
            } else if (null == offerResponse.getMaxOfferAmount() || offerResponse.getMaxOfferAmount() == 0.0f) {
                offerAmount = min(input.getTxnAmount() * offerResponse.getOfferPercentage() / 100.0f,
                        offerResponse.getMaxOfferAmount());
                bankShare = offerAmount * offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            } else {
                offerAmount = min(offerResponse.getMaxOfferAmount(),
                        offerResponse.getOfferPercentage() * input.getTxnAmount() / 100.0f);
                bankShare = offerAmount * offerResponse.getBankPercentShare() / 100.0f;
            }
            return new OfferDetails(offerResponse.getId(), offerAmount, bankShare, offerAmount - bankShare,
                    offerResponse);
        }
        return new OfferDetails("", 0.0f, 0.0f, 0.0f, null);
    }

    public List<OfferDetailsResponse> calculate(Input input, MerchantInstantDiscountConfigResp merchantConfig,
                                                Float irrpa, Boolean isBrandSubventionModel, String brandId) {
//        LOGGER.info("CashbackBO : calculate : Starts : {},{},{},{},{}", brandId, isBrandSubventionModel, irrpa,
//        merchantConfig, input);
        if (input.getBankCode().equals("HDFC") && input.getTenure() == 3 && input.getCardType().equals("DEBIT")) {
            int x = 0;
        }
        List<String> offerTypeList = getOfferTypeList(merchantConfig);

        ArrayList<OfferDetailsResponse> offerDetailsResponseList = new ArrayList<>();

        List<OfferDetails> offerDetailsListForNCEmi = null;
        Float reverseInterestAmount = 0.0f;
        Float noCostDiscount = 0.0f;
        Float additionalDiscount = 0.0f;
        Float additionalCashback = 0.0f;
        Float cashbackRate = 0f;
        Float cashback = 0f;
        Float discountRate = 0f;
        Integer effectiveTenure;
        DecimalFormat df = new DecimalFormat("0.00");
        boolean isEmiCashbackApplicable = isEmiCashbackApplicable(brandId, input.getBankCode(), isBrandSubventionModel);
        boolean instantEmiDiscount = false;
        List<OfferDetails> offerDetailsList = new ArrayList<>();
        OfferDetails offerDetails = null;
        for (String offerType : offerTypeList) {
            switch (offerType) {
                case "emiCashback":
                    if (!instantEmiDiscount && isEmiCashbackApplicable) {
//                        offerDetails = calculateV2(input); //servify multiple offers TODO
                        offerDetailsListForNCEmi = calculateEmiCashbackList(input);
//                        offerDetails.setType(offerType);
//                        cashbackRate = offerDetails.getOfferRate();
//                        if(cashbackRate > 0) offerDetailsList.add(offerDetails);
                        boolean nonEffectiveOfferFound = false;
                        for (OfferDetails curOfferDetails : offerDetailsListForNCEmi) {
                            if (curOfferDetails.getOfferRate() > 0 &&
                                    curOfferDetails.getOfferConstruct().getEffectiveTenure() == null) {
                                nonEffectiveOfferFound = true;
                            }
                        }
                        reverseInterestAmount = Util.getReverseInterestAmount(input.getTenure(),
                                input.getTxnAmount() - additionalDiscount, irrpa);
                        Float cashbackCur = 0.0f;
                        for (OfferDetails curOfferDetails : offerDetailsListForNCEmi) {
                            curOfferDetails.setType(offerType);
                            cashbackRate = curOfferDetails.getOfferRate();
                            if (cashbackRate > 0 || (!nonEffectiveOfferFound && cashbackRate == 0.0f &&
                                    (curOfferDetails.getOfferConstruct() == null ||
                                            curOfferDetails.getOfferConstruct().getEffectiveTenure() == null))) {
                                List<OfferDetails> offerDetailsListForResponse = new ArrayList<>();
                                for (OfferDetails cur : offerDetailsList) {
                                    if (!cur.getType().equals("merchantDiscount")) {
                                        offerDetailsListForResponse.add(cur);
                                    }
                                }
                                cashbackCur = Math.min(
                                        curOfferDetails.getOfferRate() * (input.getTxnAmount() - additionalDiscount),
                                        reverseInterestAmount);
                                cashback = cashbackCur;
                                curOfferDetails.setOfferAmount(cashbackCur);
                                offerDetailsListForResponse.add(curOfferDetails);
                                OfferDetailsResponse offerDetailsResponse = OfferDetailsResponse.builder()
                                        .offerDetailsList(offerDetailsListForResponse)
                                        .noCostDiscount(noCostDiscount)
                                        .additionalDiscount(additionalDiscount)
                                        .additionalCashback(additionalCashback)
                                        .cashbackRate(cashbackRate)
                                        .cashback(cashback)
                                        .effectiveTenure(curOfferDetails.getOfferConstruct() != null
                                                ? curOfferDetails.getOfferConstruct().getEffectiveTenure() : null)
                                        .instantEmiDiscount(Boolean.FALSE)
                                        .discountRate(discountRate)
                                        .build();
                                offerDetailsResponseList.add(offerDetailsResponse);
                            }
                        }
                    }
                    break;
                case "brandBankAdditionalCashback":
                    if (additionalDiscount == 0.0f && isBrandSubventionModel != null && isBrandSubventionModel) {
                        offerDetails = calculateAdditionalCashback(input);
                        offerDetails.setType(offerType);
                        additionalCashback = offerDetails.getOfferAmount();
                        if (offerDetails.getOfferAmount() > 0) offerDetailsList.add(offerDetails);
                    }
                    break;
                case "emiInstantDiscount":
                    if (isEmiCashbackApplicable) {
                        reverseInterestAmount = Util.getReverseInterestAmount(input.getTenure(),
                                input.getTxnAmount() - additionalDiscount, irrpa);
                        //TODO: IRRPA : Brand/Non-Brand
//                      offerDetails = calculateEmiInstantDiscount(input);
                        offerDetailsListForNCEmi = calculateEmiInstantDiscountList(input);
                        Float noCostDiscountCur = 0.0f;
                        for (OfferDetails curOfferDetails : offerDetailsListForNCEmi) {
                            curOfferDetails.setType(offerType);
                            noCostDiscountCur = Math.min(
                                    curOfferDetails.getOfferRate() * (input.getTxnAmount() - additionalDiscount),
                                    reverseInterestAmount);
                            instantEmiDiscount = noCostDiscountCur > 0 || instantEmiDiscount;
                            curOfferDetails.setOfferAmount(noCostDiscountCur);
                            if (noCostDiscountCur > 0) {
//                                offerDetailsList.add(offerDetails);
                                noCostDiscount = noCostDiscountCur;
                                List<OfferDetails> offerDetailsListForResponse = new ArrayList<>();
                                for (OfferDetails cur : offerDetailsList) {
                                    if (!cur.getType().equals("merchantDiscount")) {
                                        offerDetailsListForResponse.add(cur);
                                    }
                                }
                                offerDetailsListForResponse.add(curOfferDetails);
                                OfferDetailsResponse offerDetailsResponse = OfferDetailsResponse.builder()
                                        .offerDetailsList(offerDetailsListForResponse)
                                        .noCostDiscount(noCostDiscount)
                                        .additionalDiscount(additionalDiscount)
                                        .additionalCashback(additionalCashback)
                                        .cashbackRate(cashbackRate)
                                        .cashback(cashback)
                                        .effectiveTenure(curOfferDetails.getOfferConstruct().getEffectiveTenure())
                                        .instantEmiDiscount(Boolean.TRUE)
                                        .discountRate(discountRate)
                                        .build();
                                offerDetailsResponseList.add(offerDetailsResponse);
                            }
                        }

//                        offerDetails.setType(offerType);
//                        noCostDiscount = Math.min(offerDetails.getOfferRate() *
//                                (input.getTxnAmount() - additionalDiscount), reverseInterestAmount);
//                        instantEmiDiscount = noCostDiscount > 0;
//                        offerDetails.setOfferAmount(noCostDiscount);
//                        if(instantEmiDiscount) offerDetailsList.add(offerDetails);
                    }
                    break;
                case "additionalInstantDiscount":
                    if (isBrandSubventionModel != null && isBrandSubventionModel) {
                        offerDetails = calculateAdditionalInstantDiscount(input);
                        offerDetails.setType(offerType);
                        additionalDiscount = offerDetails.getOfferAmount();
                        if (offerDetails.getOfferAmount() > 0) offerDetailsList.add(offerDetails);
                    }
                    break;
//                case "merchantDiscount":
//                    if (cashbackRate <= 0f) {
//                        offerDetails = offerBrmsBO.calculateV3(input);
//                        if (offerDetails.getOfferRate() > 0 && noCostDiscount == 0.0f) {
//                            noCostDiscount =
//                                    Math.min(offerDetails.getOfferRate() * (input.getTxnAmount() - additionalDiscount),
//                                            Util.getReverseInterestAmount(input.getTenure(),
//                                                    input.getTxnAmount() - additionalDiscount, irrpa));
//                            discountRate = offerDetails.getOfferRate();
//                            offerDetails.setType(offerType);
//                            offerDetails.setOfferAmount(noCostDiscount);
//                            offerDetailsList.add(offerDetails);
//                        }
//                    }
//                    break;
                default:
                    break;
            }
        }
        if (offerDetailsResponseList.stream()
                    .filter(offerDetailsResponse -> offerDetailsResponse.getCashback() > 0).count() == 0 &&
                offerTypeList.contains("merchantDiscount")) {
//            LOGGER.info("CashbackBO : calculate : size 0 : {}, {}, {}, {}, {}, {}", brandId,
//            isBrandSubventionModel, irrpa,
//                    merchantConfig, input, offerDetailsResponseList);

            // calculating merchant offers only if brand cashback is not present
            List<OfferDetails> merchantOffers = offerBrmsBO.calculateMerchantOffers(input);
            if (!CollectionUtils.isEmpty(merchantOffers)) {
                offerDetailsResponseList.clear();

                for (OfferDetails merchantOffer: merchantOffers) {
                    merchantOffer.setType("merchantDiscount");
                    List<OfferDetails> offerDetailsListForResponse = new ArrayList<>();
                    for (OfferDetails cur : offerDetailsList) {
                        if (!cur.getType().equals("emiCashback")) {
                            offerDetailsListForResponse.add(cur);
                        }
                    }
                    offerDetailsListForResponse.add(merchantOffer);
                    OfferDetailsResponse offerDetailsResponse = OfferDetailsResponse.builder()
                            .offerDetailsList(offerDetailsListForResponse)
                            .noCostDiscount(noCostDiscount)
                            .additionalDiscount(additionalDiscount)
                            .additionalCashback(additionalCashback)
                            .cashbackRate(cashbackRate)
                            .cashback(cashback)
                            .effectiveTenure(merchantOffer.getOfferConstruct() != null
                                    ? merchantOffer.getOfferConstruct().getEffectiveTenure() : null)
                            .instantEmiDiscount(instantEmiDiscount)
                            .discountRate(merchantOffer.getOfferRate())
                            .build();
                    offerDetailsResponseList.add(offerDetailsResponse);
                }
            }
        }

//        return offerDetailsResponse;

//        LOGGER.info("CashbackBO : calculate : Ends : {}, {}, {}, {}, {}, {}", brandId, isBrandSubventionModel, irrpa,
//                merchantConfig, input, offerDetailsResponseList);
        return offerDetailsResponseList;
    }

    public List<String> getOfferTypeList(MerchantInstantDiscountConfigResp merchantConfig) {
        List<String> offerTypeListOld = merchantConfig != null ? merchantConfig.getOfferType() : new ArrayList<>();
        List<String> offerTypeList = new ArrayList<>();
        for (String cur : offerTypeListOld) {
            offerTypeList.add(cur);
        }

        if (offerTypeList.contains("additionalInstantDiscount")) {
            offerTypeList.remove("additionalInstantDiscount");
            offerTypeList.add(0, "additionalInstantDiscount");
            offerTypeList.add(1, "brandBankAdditionalCashback");
            offerTypeList.add(2, "emiCashback");
            offerTypeList.add(3, "merchantDiscount");
        } else {
            offerTypeList.add(0, "brandBankAdditionalCashback");
            offerTypeList.add(1, "emiCashback");
            offerTypeList.add(2, "merchantDiscount");
        }
//        offerTypeList.add("brandBankAdditionalCashback");
//        offerTypeList.add("emiCashback");
        return offerTypeList;
    }

    public void recalculateWithDownPayment(Input input, MerchantInstantDiscountConfigResp merchantConfig, Float irrpa,
                                           Boolean isBrandSubventionModel, String brandId, OfferDetailsResponse cur) {
        List<String> offerTypeList = getOfferTypeList(merchantConfig);

        Float noCostDiscount = 0.0f;
        Float additionalDiscount = 0.0f;
        Float additionalCashback = 0.0f;
        Float cashback = 0f;

        List<OfferDetails> newOfferDetailsList = new ArrayList<>();

        for (String offerType : offerTypeList) {
            OfferDetails offerDetails = null;
            switch (offerType) {
                case "additionalInstantDiscount":
                    if (isBrandSubventionModel != null && isBrandSubventionModel) {
                        offerDetails = calculateAdditionalInstantDiscount(input);
                        offerDetails.setType(offerType);
                        additionalDiscount = offerDetails.getOfferAmount();
                        if (offerDetails.getOfferAmount() > 0) newOfferDetailsList.add(offerDetails);
                    }
                    break;
                case "brandBankAdditionalCashback":
                    if (isBrandSubventionModel != null && isBrandSubventionModel) {
                        offerDetails = calculateAdditionalCashback(input);
                        offerDetails.setType(offerType);
                        additionalCashback = offerDetails.getOfferAmount();
                        if (offerDetails.getOfferAmount() > 0) newOfferDetailsList.add(offerDetails);
                    }
                    break;
                case "emiInstantDiscount":
                    offerDetails = cur.getOfferDetailsList()
                            .stream()
                            .filter(o -> offerType.equals(o.getType()))
                            .findFirst()
                            .orElse(null);
                    if (offerDetails != null) {
                        Integer tenure = offerDetails.getOfferConstruct() != null &&
                                offerDetails.getOfferConstruct().getEffectiveTenure() != null &&
                                offerDetails.getOfferConstruct().getEffectiveTenure() > 0
                                ? offerDetails.getOfferConstruct().getEffectiveTenure() : input.getTenure();
                        Float reverseInterestAmount =
                                Util.getReverseInterestAmount(tenure, input.getTxnAmount() - additionalDiscount, irrpa);
                        noCostDiscount =
                                Math.min(offerDetails.getOfferRate() * (input.getTxnAmount() - additionalDiscount),
                                        reverseInterestAmount);
                        offerDetails.setOfferAmount(noCostDiscount);
                        newOfferDetailsList.add(offerDetails);
                    }
                    break;
                case "emiCashback":
                    offerDetails = cur.getOfferDetailsList()
                            .stream()
                            .filter(o -> offerType.equals(o.getType()))
                            .findFirst()
                            .orElse(null);
                    if (offerDetails != null) {
                        Integer tenure = offerDetails.getOfferConstruct() != null &&
                                offerDetails.getOfferConstruct().getEffectiveTenure() != null &&
                                offerDetails.getOfferConstruct().getEffectiveTenure() > 0
                                ? offerDetails.getOfferConstruct().getEffectiveTenure() : input.getTenure();
                        Float reverseInterestAmount =
                                Util.getReverseInterestAmount(tenure, input.getTxnAmount() - additionalDiscount, irrpa);
                        cashback = Math.min(offerDetails.getOfferRate() * (input.getTxnAmount() - additionalDiscount),
                                reverseInterestAmount);
                        offerDetails.setOfferAmount(cashback);
                        newOfferDetailsList.add(offerDetails);
                    }
                    break;
                default:
                    offerDetails = cur.getOfferDetailsList()
                            .stream()
                            .filter(o -> offerType.equals(o.getType()))
                            .findFirst()
                            .orElse(null);
                    if (offerDetails != null) {
                        newOfferDetailsList.add(offerDetails);
                    }
            }
        }
        cur.setNoCostDiscount(noCostDiscount);
        cur.setAdditionalDiscount(additionalDiscount);
        cur.setAdditionalCashback(additionalCashback);
        cur.setCashback(cashback);
        cur.setOfferDetailsList(newOfferDetailsList);
    }

    public OfferResponse getMarginOfferConfig(Input input) {
        if (input == null || input.getBrandSubventions() == null || input.getBrandSubventions().size() == 0) {
            return null;
        }
        List<OfferResponse> applicableOffers = input.getBrandSubventions()
                .stream()
                .filter(offer -> "marginDownPayment".equalsIgnoreCase(offer.getType()))
                .filter(offer -> isProductCriteria(input.getBrandProductId(), offer.getProductId(),
                        offer.getProductIds()))
                .collect(Collectors.toList());
        Instant currentDate = Instant.now();
        for (OfferResponse offerResponse : applicableOffers) {
            if (input.getBankCode().equals(offerResponse.getBankCode()) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    input.getTenure().equals(offerResponse.getTenure())) {
                if ((offerResponse.getValidFrom() == null || currentDate.isAfter(offerResponse.getValidFrom())) &&
                        (offerResponse.getValidTo() == null || currentDate.isBefore(offerResponse.getValidTo()))) {
                    return offerResponse;
                }
            }
        }
        for (OfferResponse offerResponse : applicableOffers) {
            if ((offerResponse.getBankCode() == null || input.getBankCode().equals(offerResponse.getBankCode())) &&
                    input.getCardType().equals(offerResponse.getCardType()) &&
                    (offerResponse.getTenure() == -1 || input.getTenure().equals(offerResponse.getTenure()))) {
                if ((offerResponse.getValidFrom() == null || currentDate.isAfter(offerResponse.getValidFrom())) &&
                        (offerResponse.getValidTo() == null || currentDate.isBefore(offerResponse.getValidTo()))) {
                    return offerResponse;
                }
            }
        }
        return null;
    }

    public boolean isStateCriteriaSatisfied(String merchantState, List<String> offerApplicableStates,
                                            List<String> offerExclusionStates) {
        if (CollectionUtils.isEmpty(offerApplicableStates) && CollectionUtils.isEmpty(offerExclusionStates)) {
            return true;
        }
        return (merchantState != null) && (CollectionUtils.isEmpty(offerApplicableStates) ||
                offerApplicableStates.stream().anyMatch(s -> s.equalsIgnoreCase(merchantState))) &&
                (CollectionUtils.isEmpty(offerExclusionStates) ||
                        !offerExclusionStates.stream().anyMatch(s -> s.equalsIgnoreCase(merchantState)));
    }

//    public static void main(String[] args) {
//        Input input = Input.builder()
//                .tenure(12)
//                .bankCode("KKBK")
//                .cardType("DEBIT")
//                .productId("")
//                .brandProductId("brpd124")
//                .brandSubventions(Arrays.asList(new OfferResponse(
//                                "123456", 9, 9.0f, true,
//                                "DEBIT", "", Arrays.asList("brpd123", "brpd124", "brpd125"),
//                                "KKBK", null, null, null, null,
//                                null, null, null, null,
//                                null, null, null),
//                        new OfferResponse(
//                                "1234567", 12, 12.0f, true,
//                                "DEBIT", "", Arrays.asList("brpd123", "brpd125", "brpd126"),
//                                "KKBK", null, null, null, null,
//                                null, null, null, null,
//                                null, null, null),
//                        new OfferResponse(
//                                "1234567", 12, 12.0f, true,
//                                "DEBIT", "", Arrays.asList("brpd123", "brpd124", "brpd126"),
//                                "KKBK", null, null, null, null,
//                                null, null, null, null,
//                                null, null, 3),
//                        new OfferResponse(
//                                "1234567", 12, 12.0f, true,
//                                "DEBIT", "", Arrays.asList("brpd123", "brpd126"),
//                                "KKBK", null, null, null, null,
//                                null, null, null, null,
//                                null, null, 12),
//                        new OfferResponse(
//                                "1234567", 12, 12.0f, true,
//                                "DEBIT", "", null,
//                                "KKBK", null, null, null, null,
//                                null, null, null, null,
//                                null, null, 6)))
//                .build();
//       List<Output> outputs1 = calculateOffers(input);
//       for(Output o1 : outputs1){
//           System.out.println(o1);
//       }
//    }
}
