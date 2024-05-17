package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.CashbackBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.AdditionalOfferResponse;
import com.freewayemi.merchant.dto.request.AllApplicableOfferRequest;
import com.freewayemi.merchant.dto.request.ApplicableOfferRequest;
import com.freewayemi.merchant.dto.request.OfferRequest;
import com.freewayemi.merchant.entity.MerchantInstantDiscountConfiguration;
import com.freewayemi.merchant.entity.MerchantSegmentMapping;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.entity.Offer;
import com.freewayemi.merchant.repository.MerchantSegmentMappingRepository;
import com.freewayemi.merchant.repository.MerchantUserRepository;
import com.freewayemi.merchant.repository.OfferRepository;
import com.freewayemi.merchant.utils.RedisKeyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.JavaStlUtil.distinctByKeys;
import static org.apache.commons.lang3.ObjectUtils.min;

@Component
public class OfferBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferBO.class);
    private final OfferRepository offerRepository;
    private final PaymentServiceBO paymentServiceBO;
    private final CashbackBO cashbackBO;
    private final MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO;
    private final MerchantUserRepository merchantUserRepository;
    private final MerchantSegmentMappingRepository merchantSegmentMappingRepository;
    private final CacheBO cacheBO;
    private final boolean merchantOfferCachingEnabled;

    private final Gson gson = new Gson();

    public Offer get(String offer) {
        return offerRepository.findById(offer).orElseThrow(() -> new FreewayException("Offer Not Found."));
    }

    public void updateOffers(String merchantId, List<Offer> offers) {
        offerRepository.findByMerchantId(merchantId).ifPresent(offerRepository::deleteAll);
        cacheBO.removeFromCache(RedisKeyUtil.getMerchantOffersKey(merchantId));
        offerRepository.saveAll(offers);
    }

    public Boolean isISGProviderForCredit(Offer offer, List<PaymentProviderInfo> providers) {
        if (!offer.getCardType().equals("CREDIT")) return true;

        for (PaymentProviderInfo providerInfo : providers) {
            if ((null == providerInfo.getBank() || providerInfo.getBank().getCode().equals(offer.getBankCode())) &&
                    (null == providerInfo.getType() || providerInfo.getType().name().equals(offer.getCardType())) &&
                    (PaymentProviderEnum.isgpg == providerInfo.getProvider() ||
                            PaymentProviderEnum.cashfreepg == providerInfo.getProvider() ||
                            PaymentProviderEnum.lyrapg == providerInfo.getProvider() ||
                            PaymentProviderEnum.easebuzzpg == providerInfo.getProvider() ||
                            PaymentProviderEnum.paymentmockpg == providerInfo.getProvider() ||
                            PaymentProviderEnum.ccavenuepg == providerInfo.getProvider() ||
                            PaymentProviderEnum.ccavenueemipg == providerInfo.getProvider()) && !providerInfo.getDisabled()) {
                return true;
            }
        }
        return false;
    }

    public AdditionalOfferResponse getOfferResponse(Offer offer, Float amount) {
        if (null != offer.getMinAmount() && amount < offer.getMinAmount()) return null;

        Float offerPrice = 0.0f;
        if (null == offer.getOfferPercentage() || offer.getOfferPercentage() == 0.0f) {
            if (null != offer.getMaxOfferAmount() && offer.getMaxOfferAmount() > 0) {
                offerPrice = offer.getMaxOfferAmount();
            } else {
                return null;
            }
        } else if (null == offer.getMaxOfferAmount() || offer.getMaxOfferAmount() == 0.0f) {
            offerPrice = min(amount * offer.getOfferPercentage() / 100.0f, offer.getMaxOfferAmount());
        } else {
            offerPrice = min(amount * offer.getOfferPercentage() / 100.0f, offer.getMaxOfferAmount());
        }
        return AdditionalOfferResponse.builder()
                .offerPrice(offerPrice)
                .bankName(BankEnum.getBankNameFromCode(offer.getBankCode()))
                .cardType(offer.getCardType()).offerType(offer.getType())
                .offerSubType(offer.getType().equals(OfferType.additionalInstantDiscount.name()) ? "INSTANTCASHBACK" : "ADDITIONALCASHBACK")
                .validFrom(offer.getValidFrom())
                .validTo(offer.getValidTo())
                .build();
    }

    public List<AdditionalOfferResponse> getAdditionalOffersForProduct(String productId, String brandId,
                                                                       String merchantId, Float amount) {
        List<Offer> offers = null;
        String partner = merchantUserRepository.findById(merchantId).map(MerchantUser::getPartner).orElse(null);
        if (isMerchantAdditionalInstantDiscountEnabled(merchantId, brandId)) {
            offers = offerRepository.findByBrandIdAndTypeAndProductIdIsValid(brandId, OfferType.additionalInstantDiscount.name(),
                    productId, Instant.now(), true, effectivePartner(partner)).orElse(new ArrayList<>());
        }
        if (CollectionUtils.isEmpty(offers)) {
            offers = offerRepository.findByBrandIdAndTypeAndProductIdIsValid(brandId, OfferType.brandBankAdditionalCashback.name(),
                    productId, Instant.now(), true, effectivePartner(partner)).orElse(new ArrayList<>());
        }
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(merchantId);
        List<String> merchantSegmentIds = getSegmentIdsForMerchant(merchantId);
        List<String> finalMerchantSegmentIds = isOfferPresentForSegments(offers, merchantSegmentIds, productId) ?
                merchantSegmentIds : null;
        return offers.stream()
                .filter(offer -> isSegmentCriteria(offer, finalMerchantSegmentIds))
                .filter(offer -> isISGProviderForCredit(offer, providers))
                .map(offer -> getOfferResponse(offer, amount))
                .filter(offer -> null != offer)
                .filter(distinctByKeys(offer -> offer.getCardType() + offer.getBankName()))
                .collect(Collectors.toList());
    }

    public enum OfferType {
        global,
        category,
        merchant,
        online,
        brandBankAdditionalCashback,
        emiCashback,
        emiInstantDiscount,
        additionalInstantDiscount,
        merchantDiscount
    }

    @Autowired
    public OfferBO(OfferRepository offerRepository, PaymentServiceBO paymentServiceBO, CashbackBO cashbackBO, MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO,
                   MerchantUserRepository merchantUserRepository, MerchantSegmentMappingRepository merchantSegmentMappingRepository,
                   CacheBO cacheBO, @Value("${merchant.offers.caching.enabled: false}") boolean merchantOfferCachingEnabled) {

        this.offerRepository = offerRepository;
        this.paymentServiceBO = paymentServiceBO;
        this.cashbackBO = cashbackBO;
        this.merchantInstantDiscountConfigurationBO = merchantInstantDiscountConfigurationBO;
        this.merchantUserRepository = merchantUserRepository;
        this.merchantSegmentMappingRepository = merchantSegmentMappingRepository;
        this.cacheBO = cacheBO;
        this.merchantOfferCachingEnabled = merchantOfferCachingEnabled;
    }

    public List<OfferResponse> getAll() {
        return offerRepository.findAll()
                .stream()
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(), offer.getSubvention(),
                        null, null, null, null, null, offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(),
                        null, null, null, null, null, null,
                        null, null, null, offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()))
                .collect(Collectors.toList());
    }

    public void create(OfferRequest request) {
        if (OfferType.category.equals(request.getCategory()) && StringUtils.isEmpty(request.getCategory()))
            throw new FreewayException("Category not valid.");
        if (OfferType.merchant.equals(request.getType()) && StringUtils.isEmpty(request.getMerchantId()))
            throw new RuntimeException("Merchant not valid.");
        if (OfferType.online.equals(request.getType()) && StringUtils.isEmpty(request.getMerchantId()))
            throw new RuntimeException("Enter merchant details.");
        Offer offer = new Offer();
        offer.setType(StringUtils.isEmpty(request.getType()) ? OfferType.global.name() : request.getType());
        offer.setCategory(request.getCategory());
        offer.setSubvention(request.getSubvention());
        offer.setMerchantId(request.getMerchantId());
        offer.setTenure(request.getTenure());
        offer.setCardType(request.getCardType());
        offer.setBankCode(request.getBankCode());
        offer.setProductId(request.getProduct());
        offer.setValidFrom(request.getValidFrom());
        offer.setValidTo(request.getValidTo());
        offerRepository.save(offer);
    }

    public List<OfferResponse> getPgMerchantOffers(String pgMerchant) {
        LOGGER.info("OfferResponses: {}", pgMerchant);
        if (Boolean.TRUE.equals(merchantOfferCachingEnabled)) {
            try {
                String fromCache = cacheBO.getFromCache(RedisKeyUtil.getMerchantOffersKey(pgMerchant));
                if (StringUtils.hasText(fromCache)) {
                    return gson.fromJson(fromCache, new TypeToken<ArrayList<OfferResponse>>() {}.getType());
                }
            } catch (Exception ex) {
                LOGGER.error("Error getting merchant offers from cache", ex);
            }
        }
        DecimalFormat df = new DecimalFormat("0.000");
        List<OfferResponse> offerResponses = offerRepository.findByMerchantId(pgMerchant)
                .get()
                .stream()
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(),
                        Float.valueOf(df.format(offer.getSubvention())), null, offer.getCardType(),
                        offer.getProductId(), null, offer.getBankCode(), offer.getValidFrom(), offer.getValidTo(),
                        offer.getMinAmount(), null, null, null, null, null, null, null, null, offer.getVelocity(),
                        offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()))
                .collect(Collectors.toList());
        if (merchantOfferCachingEnabled) {
            cacheBO.putInCache(RedisKeyUtil.getMerchantOffersKey(pgMerchant), gson.toJson(offerResponses), false);
        }
        LOGGER.info("OfferResponses: {}", offerResponses);
        return offerResponses;
    }

    public List<OfferResponse> getBrandSubventions(String brandId, String merchantId, String partner) {
        if (StringUtils.isEmpty(brandId)) return new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        List<String> merchantSegmentIds = getSegmentIdsForMerchant(merchantId);
        List<Offer> offers = offerRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE, effectivePartner(partner))
                .orElse(new ArrayList<>());
        List<String> finalMerchantSegmentIds = isOfferPresentForSegments(offers, merchantSegmentIds, null) ?
                merchantSegmentIds : null;
        return offers
                .stream()
                .filter(offer -> isSegmentCriteria(offer, finalMerchantSegmentIds))
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(),
                        Float.valueOf(df.format(offer.getSubvention())), offer.getIsValid(), offer.getCardType(),
                        offer.getProductId(), offer.getProductIds(), offer.getBankCode(), offer.getValidFrom(),
                        offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(), offer.getOfferPercentage(),
                        offer.getBankPercentShare(), offer.getBankShareAmt(), offer.getBrandPercentShare(),
                        offer.getBrandShareAmt(), offer.getMaxBankShare(), offer.getMaxBrandShare(),
                        offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()).setType(
                        offer.getType()))
                .collect(Collectors.toList());
    }

    public List<OfferResponse> getBrandSubventionsForProduct(String brandId, String brandProductId, String merchantId, String partner) {
        if (StringUtils.isEmpty(brandProductId)) {
            return getBrandSubventions(brandId, merchantId, partner);
        }
        if (StringUtils.isEmpty(brandId)) return new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        List<Offer> offers = findByBrandId(brandId, partner);
        if (offers == null) {
            return new ArrayList<>();
        }
        List<String> merchantSegmentIds = getSegmentIdsForMerchant(merchantId);
        List<String> finalMerchantSegmentIds = isOfferPresentForSegments(offers, merchantSegmentIds, brandProductId) ?
                merchantSegmentIds : null;
        return offers
                .stream()
                .filter(offer -> cashbackBO.isProductCriteria(brandProductId, offer.getProductId(), offer.getProductIds()))
                .filter(offer -> isSegmentCriteria(offer, finalMerchantSegmentIds))
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(),
                        Float.valueOf(df.format(offer.getSubvention())), offer.getIsValid(), offer.getCardType(),
                        offer.getProductId(), offer.getProductIds(), offer.getBankCode(), offer.getValidFrom(),
                        offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(), offer.getOfferPercentage(),
                        offer.getBankPercentShare(), offer.getBankShareAmt(), offer.getBrandPercentShare(),
                        offer.getBrandShareAmt(), offer.getMaxBankShare(), offer.getMaxBrandShare(),
                        offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()).setType(
                        offer.getType()))
                .collect(Collectors.toList());
    }

    public List<OfferResponse> getBrandSubventions(String brandId, String cardType, String merchantId, String partner) {
        if (StringUtils.isEmpty(brandId)) return new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        List<Offer> offers = null;
        if (StringUtils.hasText(cardType)) {
            offers = offerRepository.findByBrandIdAndCardTypeAndIsValidAndType(brandId, cardType, Instant.now(),
                    true, nceOfferTypes(), effectivePartner(partner)).orElse(new ArrayList<>());
        } else {
            offers = offerRepository.findByBrandIdAndIsValidAndType(brandId, Instant.now(), true, nceOfferTypes(),
                    effectivePartner(partner)).orElse(new ArrayList<>());
        }
        List<String> merchantSegmentIds = getSegmentIdsForMerchant(merchantId);
        List<String> finalMerchantSegmentIds = isOfferPresentForSegments(offers, merchantSegmentIds, null) ?
                merchantSegmentIds : null;
        return offers.stream()
                .filter(offer -> isSegmentCriteria(offer, finalMerchantSegmentIds))
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(),
                        Float.valueOf(df.format(offer.getSubvention())), offer.getIsValid(), offer.getCardType(),
                        offer.getProductId(), offer.getProductIds(), offer.getBankCode(), offer.getValidFrom(),
                        offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(), offer.getOfferPercentage(),
                        offer.getBankPercentShare(), offer.getBankShareAmt(), offer.getBrandPercentShare(),
                        offer.getBrandShareAmt(), offer.getMaxBankShare(), offer.getMaxBrandShare(),
                        offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()).setType(
                        offer.getType()))
                .collect(Collectors.toList());
    }

    public List<OfferResponse> getBrandSubventionsWithoutAdditionalOffers(String brandId, String merchantId,
                                                                          Boolean isValid, String partner) {
        if (StringUtils.isEmpty(brandId)) return new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        List<Offer> offers = offerRepository.findByBrandIdAndIsValidAndNotAdditionalCashback(brandId, Instant.now(),
                isValid, effectivePartner(partner)).orElse(new ArrayList<>());
        List<String> merchantSegmentIds = getSegmentIdsForMerchant(merchantId);
        List<String> finalMerchantSegmentIds = isOfferPresentForSegments(offers, merchantSegmentIds, null) ?
                merchantSegmentIds : null;
        return offers
                .stream()
                .filter(offer -> isSegmentCriteria(offer, finalMerchantSegmentIds))
                .map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(),
                        Float.valueOf(df.format(offer.getSubvention())), offer.getIsValid(), offer.getCardType(),
                        offer.getProductId(), offer.getProductIds(), offer.getBankCode(), offer.getValidFrom(),
                        offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(), offer.getOfferPercentage(),
                        offer.getBankPercentShare(), offer.getBankShareAmt(), offer.getBrandPercentShare(),
                        offer.getBrandShareAmt(), offer.getMaxBankShare(), offer.getMaxBrandShare(),
                        offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                        offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                        offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()).setType(
                        offer.getType()))
                .collect(Collectors.toList());
    }

    public Boolean isOfferAvailable(String brandId, Boolean isValid, String partner){
        Boolean isOfferAvailable = Boolean.FALSE;
        long offers = offerRepository.countByBrandIdAndIsValidAndNotAdditionalCashback(brandId, Instant.now(),
                isValid,partner);
        if (offers > 0){
            isOfferAvailable = Boolean.TRUE;
        }
        return isOfferAvailable;
    }

    public List<Offer> findByBrandIds(String[] brandIds, String partner) {
        return offerRepository.findByBrandIds(brandIds, Instant.now(), effectivePartner(partner)).orElse(null);
    }

    public List<Offer> findByBrandId(String brandId, String partner) {

        return offerRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE, effectivePartner(partner)).orElse(null);
    }

    public List<Offer> findByBrandIdWithSubvention(String brandId, String partner) {
        return offerRepository.findByBrandIdWithSubvention(brandId, Instant.now(), Boolean.TRUE, effectivePartner(partner))
                .orElse(new ArrayList<>());
    }

    public List<OfferDetailsResponse> getApplicableOffers(ApplicableOfferRequest applicableOfferRequest) {
        TransactionResponse tr = paymentServiceBO.getTransactionById(applicableOfferRequest.getTransactionId());
        Input input = Util.getInput(tr, applicableOfferRequest.getEffectiveCardType(), applicableOfferRequest.getBankEnum()
                , applicableOfferRequest.getEmiTenure(), null);
        return cashbackBO.calculate(input,
                tr.getMerchantInstantDiscountConfigResp(), applicableOfferRequest.getIrrpa(),
                tr.getIsBrandSubventionModel(), applicableOfferRequest.getBrandId());
    }

    public AllOfferDetailsResponse getAllApplicableOffers(AllApplicableOfferRequest allApplicableOfferRequest) {
        LOGGER.info("start transaction id : {}", allApplicableOfferRequest.getTransactionId());
        TransactionResponse tr = paymentServiceBO.getTransactionById(allApplicableOfferRequest.getTransactionId());
        ArrayList<OfferDetailsSingleCardResponse> offerDetailsSingleCardResponseList = new ArrayList<>();
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(tr.getMerchantId());
        for (ApplicableOfferRequest applicableOfferRequest : allApplicableOfferRequest.getApplicableOfferRequestList()) {
            Input input = Util.getInput(tr, applicableOfferRequest.getEffectiveCardType(), applicableOfferRequest.getBankEnum()
                    , applicableOfferRequest.getEmiTenure(), null);
            input.setProviders(providers);

            List<OfferDetailsResponse> offerDetailsResponseList = cashbackBO.calculate(input,
                    tr.getMerchantInstantDiscountConfigResp(), applicableOfferRequest.getIrrpa(),
                    tr.getIsBrandSubventionModel(), applicableOfferRequest.getBrandId());
            String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                    "_" + applicableOfferRequest.getEmiTenure();
            if (offerDetailsResponseList.size() > 0) {
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseList)
                        .build());
            }
        }
        LOGGER.info("end transaction id : {}", allApplicableOfferRequest.getTransactionId());
        return new AllOfferDetailsResponse(
                allApplicableOfferRequest.getBrandId(),
                allApplicableOfferRequest.getTransactionId(),
                offerDetailsSingleCardResponseList);
    }

    // with margin money
    public AllOfferDetailsResponse getAllApplicableOffersV2(AllApplicableOfferRequest allApplicableOfferRequest) {
        TransactionResponse tr = paymentServiceBO.getTransactionById(allApplicableOfferRequest.getTransactionId());
        ArrayList<OfferDetailsSingleCardResponse> offerDetailsSingleCardResponseList = new ArrayList<>();
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(tr.getMerchantId());
        List<String> availableDownPaymentOptions = tr.getAdditionInfo() != null &&
                tr.getAdditionInfo().getBrandInfo() != null &&
                tr.getAdditionInfo().getBrandInfo().getMarginMoneyConfig() != null ?
                tr.getAdditionInfo().getBrandInfo().getMarginMoneyConfig().getDownPaymentAvailableOptions() : null;
        if (CollectionUtils.isEmpty(availableDownPaymentOptions)) {
            availableDownPaymentOptions = new ArrayList<>();
            availableDownPaymentOptions.add("ZERO");
        }

        String merchantState = getMerchantState(allApplicableOfferRequest, tr);

        boolean merchantDownPaymentEnabled = tr.getDownPaymentEnabled() != null && tr.getDownPaymentEnabled();
        for (ApplicableOfferRequest applicableOfferRequest : allApplicableOfferRequest.getApplicableOfferRequestList()) {
            Input input = Util.getInput(tr, applicableOfferRequest.getEffectiveCardType(), applicableOfferRequest.getBankEnum()
                    , applicableOfferRequest.getEmiTenure(), null, merchantState);
            input.setProviders(providers);

            List<OfferDetailsResponse> offerDetailsResponseList = cashbackBO.calculate(input,
                    tr.getMerchantInstantDiscountConfigResp(), applicableOfferRequest.getIrrpa(),
                    tr.getIsBrandSubventionModel(), applicableOfferRequest.getBrandId());

            List<OfferDetailsResponse> offerDetailsResponseListZero = new ArrayList<>();
            List<OfferDetailsResponse> offerDetailsResponseListFixed = new ArrayList<>();
            List<OfferDetailsResponse> offerDetailsResponseListMargin = new ArrayList<>();

            for (OfferDetailsResponse cur : offerDetailsResponseList) {
                if (cur.getEffectiveTenure() != null) {
                    cur.setDownPaymentType("FIXED");
                    Float downPaymentAmount = Util.calculateFixedDownPayment(input.getTxnAmount(), input.getTenure(),
                            cur.getEffectiveTenure());
                    if (downPaymentAmount > 0f) {
                        Input newInput = Util.getInput(tr, applicableOfferRequest.getEffectiveCardType(),
                                applicableOfferRequest.getBankEnum(), applicableOfferRequest.getEmiTenure(),
                                cur.getEffectiveTenure(), merchantState);
                        newInput.setTxnAmount(newInput.getTxnAmount() - downPaymentAmount);
                        newInput.setProviders(providers);

                        cashbackBO.recalculateWithDownPayment(newInput, tr.getMerchantInstantDiscountConfigResp(),
                                applicableOfferRequest.getIrrpa(), tr.getIsBrandSubventionModel(),
                                applicableOfferRequest.getBrandId(), cur);

                        cur.setDownPaymentAmount(downPaymentAmount);
                    }
                    if (availableDownPaymentOptions.contains("FIXED") ||
                            (cur.getDiscountRate() != null && cur.getDiscountRate() > 0.0f)) {
                        offerDetailsResponseListFixed.add(cur);
                    }
                } else {
                    cur.setDownPaymentType("ZERO");
                    offerDetailsResponseListZero.add(cur);

                    // margin calculation
                    OfferResponse offerResponseMargin = cashbackBO.getMarginOfferConfig(input);
                    Float marginDownPaymentAmount = allApplicableOfferRequest.getMarginDownPaymentAmount();
                    if (offerResponseMargin != null &&
                            (marginDownPaymentAmount == null ||
                                    (marginDownPaymentAmount >= offerResponseMargin.getMinMarginDownPaymentAmount() &&
                                            marginDownPaymentAmount <= offerResponseMargin.getMaxMarginDownPaymentAmount()))) {
                        Float downPaymentAmount = allApplicableOfferRequest.getMarginDownPaymentAmount() != null ?
                                allApplicableOfferRequest.getMarginDownPaymentAmount() :
                                offerResponseMargin.getMinMarginDownPaymentAmount();
                        List<OfferDetails> offerDetailsList = null;
                        if (cur.getOfferDetailsList() != null) {
                            offerDetailsList = cur.getOfferDetailsList().stream()
                                    .map(o -> {
                                        OfferDetails newOfferDetails = new OfferDetails(o.getOfferId(),
                                                o.getOfferAmount(), o.getBankShareAmt(), o.getBrandShareAmt(),
                                                o.getOfferConstruct());
                                        newOfferDetails.setType(o.getType());
                                        newOfferDetails.setOfferRate(o.getOfferRate());
                                        return newOfferDetails;
                                    })
                                    .collect(Collectors.toList());
                        }
                        OfferDetailsResponse margin = new OfferDetailsResponse(cur.getNoCostDiscount(), cur.getAdditionalDiscount(),
                                cur.getAdditionalCashback(), cur.getCashbackRate(), cur.getCashback(), cur.getInstantEmiDiscount(),
                                cur.getEffectiveTenure(), offerDetailsList, cur.getAdvanceDownPaymentRate(),
                                cur.getOfflineAdvanceEmiTenure(), cur.getTransactionAmount(), cur.getIrrpa(),
                                downPaymentAmount, "MARGIN", offerResponseMargin.getMinMarginDownPaymentAmount(),
                                offerResponseMargin.getMaxMarginDownPaymentAmount(), cur.getDiscountRate());

                        Input newInput = Util.getInput(tr, applicableOfferRequest.getEffectiveCardType(),
                                applicableOfferRequest.getBankEnum(), applicableOfferRequest.getEmiTenure(),
                                cur.getEffectiveTenure(), merchantState);
                        newInput.setTxnAmount(newInput.getTxnAmount() - downPaymentAmount);
                        newInput.setProviders(providers);

                        cashbackBO.recalculateWithDownPayment(newInput, tr.getMerchantInstantDiscountConfigResp(),
                                applicableOfferRequest.getIrrpa(), tr.getIsBrandSubventionModel(),
                                applicableOfferRequest.getBrandId(), margin);

                        offerDetailsResponseListMargin.add(margin);
                    }
                }
            }

            if (offerDetailsResponseListZero.size() > 0) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "ZERO";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListZero)
                        .build());
            }
            if (offerDetailsResponseListFixed.size() > 0 && merchantDownPaymentEnabled) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "FIXED";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListFixed)
                        .build());
            }
            if (offerDetailsResponseListMargin.size() > 0 && availableDownPaymentOptions.contains("MARGIN") &&
                    merchantDownPaymentEnabled) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "MARGIN";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListMargin)
                        .build());
            }
        }
        return new AllOfferDetailsResponse(
                allApplicableOfferRequest.getBrandId(),
                allApplicableOfferRequest.getTransactionId(),
                offerDetailsSingleCardResponseList);
    }

    public AllOfferDetailsResponse getAllApplicableOffersV3(AllApplicableOfferRequest allApplicableOfferRequest, MerchantResponse merchantResponse) {
        ArrayList<OfferDetailsSingleCardResponse> offerDetailsSingleCardResponseList = new ArrayList<>();
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(allApplicableOfferRequest.getMerchantId());
        List<String> availableDownPaymentOptions = merchantResponse.getBrandInfo() != null &&
                merchantResponse.getBrandInfo().getMarginMoneyConfig() != null ?
                merchantResponse.getBrandInfo().getMarginMoneyConfig().getDownPaymentAvailableOptions() : null;
        if (CollectionUtils.isEmpty(availableDownPaymentOptions)) {
            availableDownPaymentOptions = new ArrayList<>();
            availableDownPaymentOptions.add("ZERO");
        }

        String merchantState = getMerchantState(allApplicableOfferRequest);

        boolean merchantDownPaymentEnabled = merchantResponse.getDownPaymentEnabled() != null && merchantResponse.getDownPaymentEnabled();
        for (ApplicableOfferRequest applicableOfferRequest : allApplicableOfferRequest.getApplicableOfferRequestList()) {
            Input input = Util.getInputV3(merchantResponse, applicableOfferRequest.getEffectiveCardType(), applicableOfferRequest.getBankEnum()
                    , applicableOfferRequest.getEmiTenure(), null, merchantState, allApplicableOfferRequest.getProductAmount(), allApplicableOfferRequest.getBrandProductId());
            input.setProviders(providers);

            List<OfferDetailsResponse> offerDetailsResponseList = cashbackBO.calculate(input,
                    merchantResponse.getMerchantInstantDiscountConfigResp(), applicableOfferRequest.getIrrpa(),
                    merchantResponse.getIsBrandSubventionModel(), applicableOfferRequest.getBrandId());

            List<OfferDetailsResponse> offerDetailsResponseListZero = new ArrayList<>();
            List<OfferDetailsResponse> offerDetailsResponseListFixed = new ArrayList<>();
            List<OfferDetailsResponse> offerDetailsResponseListMargin = new ArrayList<>();

            for (OfferDetailsResponse cur : offerDetailsResponseList) {
                if (cur.getEffectiveTenure() != null) {
                    cur.setDownPaymentType("FIXED");
                    Float downPaymentAmount = Util.calculateFixedDownPayment(input.getTxnAmount(), input.getTenure(),
                            cur.getEffectiveTenure());
                    if (downPaymentAmount > 0f) {
                        Input newInput = Util.getInputV3(merchantResponse, applicableOfferRequest.getEffectiveCardType(),
                                applicableOfferRequest.getBankEnum(), applicableOfferRequest.getEmiTenure(),
                                cur.getEffectiveTenure(), merchantState, allApplicableOfferRequest.getProductAmount(), allApplicableOfferRequest.getBrandProductId());
                        newInput.setTxnAmount(newInput.getTxnAmount() - downPaymentAmount);
                        newInput.setProviders(providers);

                        cashbackBO.recalculateWithDownPayment(newInput, merchantResponse.getMerchantInstantDiscountConfigResp(),
                                applicableOfferRequest.getIrrpa(), merchantResponse.getIsBrandSubventionModel(),
                                applicableOfferRequest.getBrandId(), cur);

                        cur.setDownPaymentAmount(downPaymentAmount);
                    }
                    if (availableDownPaymentOptions.contains("FIXED") ||
                            (cur.getDiscountRate() != null && cur.getDiscountRate() > 0.0f)) {
                        offerDetailsResponseListFixed.add(cur);
                    }
                } else {
                    cur.setDownPaymentType("ZERO");
                    offerDetailsResponseListZero.add(cur);

                    // margin calculation
                    OfferResponse offerResponseMargin = cashbackBO.getMarginOfferConfig(input);
                    Float marginDownPaymentAmount = allApplicableOfferRequest.getMarginDownPaymentAmount();
                    if (offerResponseMargin != null &&
                            (marginDownPaymentAmount == null ||
                                    (marginDownPaymentAmount >= offerResponseMargin.getMinMarginDownPaymentAmount() &&
                                            marginDownPaymentAmount <= offerResponseMargin.getMaxMarginDownPaymentAmount()))) {
                        Float downPaymentAmount = allApplicableOfferRequest.getMarginDownPaymentAmount() != null ?
                                allApplicableOfferRequest.getMarginDownPaymentAmount() :
                                offerResponseMargin.getMinMarginDownPaymentAmount();
                        List<OfferDetails> offerDetailsList = null;
                        if (cur.getOfferDetailsList() != null) {
                            offerDetailsList = cur.getOfferDetailsList().stream()
                                    .map(o -> {
                                        OfferDetails newOfferDetails = new OfferDetails(o.getOfferId(),
                                                o.getOfferAmount(), o.getBankShareAmt(), o.getBrandShareAmt(),
                                                o.getOfferConstruct());
                                        newOfferDetails.setType(o.getType());
                                        newOfferDetails.setOfferRate(o.getOfferRate());
                                        return newOfferDetails;
                                    })
                                    .collect(Collectors.toList());
                        }
                        OfferDetailsResponse margin = new OfferDetailsResponse(cur.getNoCostDiscount(), cur.getAdditionalDiscount(),
                                cur.getAdditionalCashback(), cur.getCashbackRate(), cur.getCashback(), cur.getInstantEmiDiscount(),
                                cur.getEffectiveTenure(), offerDetailsList, cur.getAdvanceDownPaymentRate(),
                                cur.getOfflineAdvanceEmiTenure(), cur.getTransactionAmount(), cur.getIrrpa(),
                                downPaymentAmount, "MARGIN", offerResponseMargin.getMinMarginDownPaymentAmount(),
                                offerResponseMargin.getMaxMarginDownPaymentAmount(), cur.getDiscountRate());

                        Input newInput = Util.getInputV3(merchantResponse, applicableOfferRequest.getEffectiveCardType(),
                                applicableOfferRequest.getBankEnum(), applicableOfferRequest.getEmiTenure(),
                                cur.getEffectiveTenure(), merchantState, allApplicableOfferRequest.getProductAmount(), allApplicableOfferRequest.getBrandProductId());
                        newInput.setTxnAmount(newInput.getTxnAmount() - downPaymentAmount);
                        newInput.setProviders(providers);

                        cashbackBO.recalculateWithDownPayment(newInput, merchantResponse.getMerchantInstantDiscountConfigResp(),
                                applicableOfferRequest.getIrrpa(), merchantResponse.getIsBrandSubventionModel(),
                                applicableOfferRequest.getBrandId(), margin);

                        offerDetailsResponseListMargin.add(margin);
                    }
                }
            }

            if (offerDetailsResponseListZero.size() > 0) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "ZERO";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListZero)
                        .build());
            }
            if (offerDetailsResponseListFixed.size() > 0 && merchantDownPaymentEnabled) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "FIXED";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListFixed)
                        .build());
            }
            if (offerDetailsResponseListMargin.size() > 0 && availableDownPaymentOptions.contains("MARGIN") &&
                    merchantDownPaymentEnabled) {
                String key = applicableOfferRequest.getEffectiveCardType() + "_" + applicableOfferRequest.getBankEnum() +
                        "_" + applicableOfferRequest.getEmiTenure() + "_" + "MARGIN";
                offerDetailsSingleCardResponseList.add(OfferDetailsSingleCardResponse.builder()
                        .key(key)
                        .offerDetailsResponseList(offerDetailsResponseListMargin)
                        .build());
            }
        }
        return new AllOfferDetailsResponse(
                allApplicableOfferRequest.getBrandId(),
                allApplicableOfferRequest.getTransactionId(),
                offerDetailsSingleCardResponseList);
    }


    // Geographical state
    private String getMerchantState(AllApplicableOfferRequest allApplicableOfferRequest, TransactionResponse tr) {
        String merchantState = allApplicableOfferRequest.getMerchantState();
        if (merchantState == null && tr.getMerchantId() != null) {
            Optional<MerchantUser> merchantUser = merchantUserRepository.findById(tr.getMerchantId());
            if (!merchantUser.isPresent()) {
                merchantUser = merchantUserRepository.findByDisplayId(tr.getMerchantId());
            }
            merchantState = merchantUser.map(MerchantUser::getAddress)
                    .map(Address::getState)
                    .orElse(null);
        }
        return merchantState;
    }

    private String getMerchantState(AllApplicableOfferRequest allApplicableOfferRequest) {
        String merchantState = allApplicableOfferRequest.getMerchantState();
        if (merchantState == null && allApplicableOfferRequest.getMerchantId() != null) {
            Optional<MerchantUser> merchantUser = merchantUserRepository.findById(allApplicableOfferRequest.getMerchantId());
            if (!merchantUser.isPresent()) {
                merchantUser = merchantUserRepository.findByDisplayId(allApplicableOfferRequest.getMerchantId());
            }
            merchantState = merchantUser.map(MerchantUser::getAddress)
                    .map(Address::getState)
                    .orElse(null);
        }
        return merchantState;
    }

    private boolean isMerchantAdditionalInstantDiscountEnabled(String merchantId, String brandId) {
        MerchantInstantDiscountConfiguration merchantInstantDiscountConfiguration =
                merchantInstantDiscountConfigurationBO.getMerchantInstantDiscountConfiguration(merchantId, brandId);
        return null != merchantInstantDiscountConfiguration &&
                null != merchantInstantDiscountConfiguration.getOfferType() &&
                merchantInstantDiscountConfiguration.getOfferType().size() > 0 &&
                merchantInstantDiscountConfiguration.getOfferType().contains(OfferType.additionalInstantDiscount.name());
    }

    private String[] nceOfferTypes() {
        String[] types = new String[2];
        types[0] = OfferType.emiCashback.name();
        types[1] = OfferType.emiInstantDiscount.name();
        return types;
    }

    public List<String> getSegmentIdsForMerchant(String merchantId) {
        if (StringUtils.isEmpty(merchantId)) {
            return new ArrayList<>();
        }
        Optional<List<MerchantSegmentMapping>> brandMerchantConfigs = merchantSegmentMappingRepository
                .findByMerchantIdAndIsValid(merchantId, Instant.now(), Boolean.TRUE);
        if (brandMerchantConfigs.isPresent()) {
            return brandMerchantConfigs.get().stream()
                    .map(MerchantSegmentMapping::getSegmentId)
                    .filter(s -> !StringUtils.isEmpty(s))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public boolean isSegmentCriteria(Offer offer, List<String> segmentIds) {
        boolean noSegmentOffers = CollectionUtils.isEmpty(segmentIds);
        if (offer != null) {
            return noSegmentOffers ? StringUtils.isEmpty(offer.getSegmentId()) :
                    segmentIds.contains(offer.getSegmentId());
        }
        return false;
    }

    private boolean isOfferPresentForSegments(List<Offer> offers, List<String> merchantSegmentIds, String productId) {
        if (CollectionUtils.isEmpty(merchantSegmentIds)) {
            return false;
        }
        boolean checkProduct = !StringUtils.isEmpty(productId);
        return offers.stream()
                .filter(offer -> !checkProduct ||
                        cashbackBO.isProductCriteria(productId, offer.getProductId(), offer.getProductIds()))
                .filter(offer -> !StringUtils.isEmpty(offer.getSegmentId()))
                .anyMatch(offer -> merchantSegmentIds.contains(offer.getSegmentId()));
    }

    private String effectivePartner(String partner) {
        return "payment".equalsIgnoreCase(partner) ? null : partner;
    }
}
