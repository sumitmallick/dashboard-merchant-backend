package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.CashbackBO;
import com.freewayemi.merchant.commons.bo.PaymentOptionsBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.dto.PriceResponse;
import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.DefaultFilterDTO;
import com.freewayemi.merchant.dto.request.OfferBankRequest;
import com.freewayemi.merchant.dto.request.ProductOfferRequestDTO;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.repository.*;
import com.freewayemi.merchant.type.PartnerCodeEnum;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProductOfferBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductOfferBO.class);
    private final OfferRepository offerRepository;
    private final ProductOfferRepository productOfferRepository;
    private final ProductOfferCardRepository productOfferCardRepository;
    private final BrandBO brandBO;
    private final MerchantUserBO merchantUserBO;
    private final OfferBO offerBO;
    private final BrandProductBO brandProductBO;
    private final List<Integer> offerDefaultTenures;
    private final PaymentServiceBO paymentServiceBO;
    private final List<String> defaultPreApprovedBanks;
    private final PaymentOptionsBO paymentOptionsBO;
    private final CashbackBO cashbackBO;
    private final PartnerRepository partnerRepository;
    private final SchemeConfigBO schemeConfigBO;

    private final BrandProductMetaDataRepository brandProductMetaDataRepository;

    private final MerchantUserRepository merchantUserRepository;


    @Autowired
    public ProductOfferBO(OfferRepository offerRepository, ProductOfferRepository productOfferRepository,
                          ProductOfferCardRepository productOfferCardRepository, BrandBO brandBO,
                          MerchantUserBO merchantUserBO, OfferBO offerBO, BrandProductBO brandProductBO,
                          @Value("${offer.default.tenure}") List<Integer> offerDefaultTenures,
                          PaymentServiceBO paymentServiceBO,
                          @Value("${offer.filter.pre.approved.banks}") List<String> defaultPreApprovedBanks,
                          PaymentOptionsBO paymentOptionsBO, CashbackBO cashbackBO,
                          PartnerRepository partnerRepository, SchemeConfigBO schemeConfigBO, BrandProductMetaDataRepository brandProductMetaDataRepository, MerchantUserRepository merchantUserRepository) {
        this.offerRepository = offerRepository;
        this.productOfferRepository = productOfferRepository;
        this.productOfferCardRepository = productOfferCardRepository;
        this.brandBO = brandBO;
        this.merchantUserBO = merchantUserBO;
        this.offerBO = offerBO;
        this.brandProductBO = brandProductBO;
        this.offerDefaultTenures = offerDefaultTenures;
        this.paymentServiceBO = paymentServiceBO;
        this.defaultPreApprovedBanks = defaultPreApprovedBanks;
        this.paymentOptionsBO = paymentOptionsBO;
        this.cashbackBO = cashbackBO;
        this.partnerRepository = partnerRepository;
        this.schemeConfigBO = schemeConfigBO;
        this.brandProductMetaDataRepository= brandProductMetaDataRepository;
        this.merchantUserRepository = merchantUserRepository;
    }


    public OfferFiltersResponse getOffersFilterV2(OfferFiltersRequest offerFiltersRequest) {
        LOGGER.info("offerFiltersRequest : {}", offerFiltersRequest);
        OfferFiltersResponse offerFiltersResponse = new OfferFiltersResponse();
        DefaultFilterDTO defaultFilterDTO = getDefaultFilters(offerFiltersRequest.getMerchantId());
        if (defaultFilterDTO.isMerchantOffline()) {
            String[] brandIds = brandBO.getBrandId(defaultFilterDTO.getBrandIdsMap());
            List<ProductOfferCard> filterOfBrandProductOffersList = null;
            if (null != offerFiltersRequest.getFiltersOfBrands()) {
                if (null != offerFiltersRequest.getFiltersOfBrands().getCategories() &&
                        offerFiltersRequest.getFiltersOfBrands().getCategories().length > 0 &&
                        null != offerFiltersRequest.getFiltersOfBrands().getBanks() &&
                        (null != offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit() &&
                                offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit().length > 0 ||
                                null != offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit() &&
                                        offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit().length > 0)) {
                    defaultFilterDTO.getBrandIdsMap().clear();
                    if (!defaultFilterDTO.isMerchantOffline()) {
                        productOfferCardRepository.findByBrandIdAndCategoryAndIsValidAndBanksAndTypes(brandIds,
                                        offerFiltersRequest.getFiltersOfBrands().getCategories(), true,
                                        offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit(),
                                        offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit(),
                                        Instant.now(), merchantOfferTypes())
                                .orElse(null);
                    } else {
                        filterOfBrandProductOffersList =
                                productOfferCardRepository.findByBrandIdAndCategoryAndIsValidAndBanks(brandIds,
                                                offerFiltersRequest.getFiltersOfBrands().getCategories(), true,
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit(),
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit(),
                                                Instant.now())
                                        .orElse(null);
                    }
                } else if (null != offerFiltersRequest.getFiltersOfBrands().getCategories() &&
                        offerFiltersRequest.getFiltersOfBrands().getCategories().length > 0) {
                    defaultFilterDTO.getBrandIdsMap().clear();
                    if (!defaultFilterDTO.isMerchantOffline()) {
                        filterOfBrandProductOffersList =
                                productOfferCardRepository.findByBrandIdAndCategoryAndIsValidAndType(brandIds,
                                                offerFiltersRequest.getFiltersOfBrands().getCategories(), true,
                                                Instant.now(), merchantOfferTypes())
                                        .orElse(null);
                    } else {
                        filterOfBrandProductOffersList =
                                productOfferCardRepository.findByBrandIdAndCategoryAndIsValid(brandIds,
                                                offerFiltersRequest.getFiltersOfBrands().getCategories(), true,
                                                Instant.now())
                                        .orElse(null);
                    }
                } else if (null != offerFiltersRequest.getFiltersOfBrands().getBanks() &&
                        (null != offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit() &&
                                offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit().length > 0 ||
                                null != offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit() &&
                                        offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit().length > 0)) {
                    defaultFilterDTO.getBrandIdsMap().clear();
                    if (!defaultFilterDTO.isMerchantOffline()) {
                        filterOfBrandProductOffersList =
                                productOfferCardRepository.findByBrandIdAndBankAndIsValidAndTypes(brandIds, true,
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit(),
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit(),
                                                Instant.now(), merchantOfferTypes())
                                        .orElse(null);
                    }
                    else
                    {
                        filterOfBrandProductOffersList =
                                productOfferCardRepository.findByBrandIdAndBankAndIsValid(brandIds, true,
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getDebit(),
                                                offerFiltersRequest.getFiltersOfBrands().getBanks().getCredit(),
                                                Instant.now())
                                        .orElse(null);
                    }
                }
                String partner = defaultFilterDTO.getPartner();
                if(Util.isNotNull(filterOfBrandProductOffersList)) {
                    filterOfBrandProductOffersList = filterOfBrandProductOffersList.stream()
                            .filter(p -> (partner == null && p.getPartner() == null) || (partner != null && partner.equals(p.getPartner())))
                            .collect(Collectors.toList());
                }

                if (!CollectionUtils.isEmpty(filterOfBrandProductOffersList)) {
                    for (ProductOfferCard productOffers : filterOfBrandProductOffersList) {
                        defaultFilterDTO.getBrandIdsMap()
                                .put(productOffers.getVariant().getBrandId(), productOffers.getVariant().getBrandId());
                    }
                }
            }
            brandIds = brandBO.getBrandId(defaultFilterDTO.getBrandIdsMap());
            offerFiltersResponse.setBrands(
                    brandBO.getBrandResponse(new ArrayList<>(), brandBO.findByBrandId(brandIds), null, null));
            offerFiltersResponse.setSegmentOffers(null);
        } else {
            offerFiltersResponse.setBrands(new ArrayList<>());
        }

        offerFiltersResponse.setCategories(defaultFilterDTO.getProductCategoryMap()
                .values()
                .stream()
                .map((x) -> ProductCategory.builder().name(x).build())
                .collect(Collectors.toList()));
        offerFiltersResponse.setBanks(OfferBankDTO.builder()
                .credit(new ArrayList<>(defaultFilterDTO.getCcMap().values()))
                .debit(new ArrayList<>(defaultFilterDTO.getDcMap().values()))
                .build());

        return offerFiltersResponse;
    }

    private List<ProductOffer> getProductOffer(Brand brand, BrandProduct brandProduct, Offer offer,
                                               List<ProductOffer> productOffersList, List<String> defaultCreditCardBanks) {
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            if (StringUtils.hasText(brandProduct.getCategory())) {
                if (StringUtils.hasText(offer.getType()) && "brandBankAdditionalCashback".equals(offer.getType())) {
                    if (null == offer.getOfferPercentage() || offer.getOfferPercentage() == 0
                            || null == offer.getMaxOfferAmount() || offer.getMaxOfferAmount() == 0 || (null != offer.getMinAmount() && brandProduct.getAmount() < offer.getMinAmount()))
                        return productOffersList;
                    if (null == offer.getTenure())
                        offer.setTenure(-1);
                }
                ProductOfferVariant variant = getProductOfferVariant(brandProduct, offer);
                if (null != variant && null != brand) {
                    variant.setBrandName(brand.getName());
                    variant.setBrandIcon(brand.getIcon());
                    ProductOffer productOffers = new ProductOffer();
                    productOffers.setOfferId(offer.getId().toString());
                    productOffers.setVariant(variant);
                    productOffers.setType(offer.getType());
                    productOffers.setOfferDescription(offer.getOfferDescription());
                    productOffers.setIsValid(true);
                    productOffers.setValidFrom(offer.getValidFrom());
                    productOffers.setValidTo(offer.getValidTo());
                    productOffers.setMaxOfferAmount(null != offer.getMaxOfferAmount() ? offer.getMaxOfferAmount() : 0);
                    productOffers.setOfferPercentage(null != offer.getOfferPercentage() ? Float.parseFloat(df.format(offer.getOfferPercentage())) : 0);
                    productOffers.setPreApprovedCard(new ArrayList<>());
                    productOffers.setCreditCard(new ArrayList<>());
                    LOGGER.info("Request received for product offers with params : {}", offer);
                    productOffers.setSubvention(Float.parseFloat(df.format(offer.getSubvention())));
                    if (offer.getTenure() == -1) {
                        productOffers.setTenures(offerDefaultTenures);
                    } else {
                        List<Integer> tenures = new ArrayList<>();
                        tenures.add(offer.getTenure());
                        productOffers.setTenures(tenures);
                    }
                    if (StringUtils.hasText(offer.getBankCode())) {
                        List<String> banks = new ArrayList<>();
                        banks.add(offer.getBankCode());
                        if (CardTypeEnum.CREDIT.name().equals(offer.getCardType())) {
                            productOffers.setCreditCard(banks);
                        } else {
                            productOffers.setPreApprovedCard(banks);
                        }
                    } else {
                        if (CardTypeEnum.CREDIT.name().equals(offer.getCardType())) {
                            productOffers.setCreditCard(defaultCreditCardBanks);
                        } else {
                            productOffers.setPreApprovedCard(defaultPreApprovedBanks);
                        }
                    }
                    if (StringUtils.hasText(offer.getSegmentId())) {
                        productOffers.setSegmentId(offer.getSegmentId());
                    }
                    if (null != productOffers.getVariant().getAmount() && productOffers.getVariant().getAmount() > 0 &&
                            null != productOffers.getSubvention() && productOffers.getSubvention() > 0) {
                        productOffers = calculateCashback(productOffers, offer);
                        if (productOffers.getTenures().size() > 0) {
                            productOffers.setEmiType("No Cost EMI");
                            productOffersList.add(productOffers);
                        }
                    } else if (StringUtils.hasText(productOffers.getType()) && "brandBankAdditionalCashback".equals(offer.getType())) {
                        productOffers.setAdditionalCashback(calculateAdditionalCashback(productOffers));
                        productOffers.setTotalCashback(productOffers.getAdditionalCashback());
                        productOffersList.add(productOffers);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return productOffersList;
    }

    private ProductOfferVariant getProductOfferVariant(BrandProduct brandProduct, Offer offer) {
        try {
            return ProductOfferVariant.builder()
                    .productName(brandProduct.getProduct())
                    .amount(brandProduct.getAmount())
                    .brandProductId(brandProduct.getId().toString())
                    .brandId(brandProduct.getBrandId())
                    .category(brandProduct.getCategory())
                    .displayHeader(offer.getSubvention() > 0 ? brandProduct.getDisplayHeader() : "")
                    .displaySubHeader(brandProduct.getDisplaySubHeader())
                    .emiOption(brandProduct.getEmiOption())
                    .icon(brandProduct.getIcon())
                    .imageUrl(brandProduct.getImageUrl())
                    .isPopular(brandProduct.getIsPopular())
                    .isValid(brandProduct.getIsValid())
                    .minAmount(brandProduct.getMinAmount())
                    .name(brandProduct.getVariant())
                    .modelNo(brandProduct.getModelNo())
                    .popularityScore(brandProduct.getPopularityScore())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return null;
    }

    private Float calculateAdditionalCashback(ProductOffer productOffers) {
        float additionalCashback = 0.0f;
        try {
            if (null != productOffers.getOfferPercentage() && productOffers.getOfferPercentage() > 0 &&
                    null != productOffers.getMaxOfferAmount() && productOffers.getMaxOfferAmount() > 0) {
                additionalCashback =
                        productOffers.getVariant().getAmount() * (productOffers.getOfferPercentage() / 100);
                if (additionalCashback > productOffers.getMaxOfferAmount()) {
                    additionalCashback = productOffers.getMaxOfferAmount();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return additionalCashback;
    }

    private ProductOffer calculateCashback(ProductOffer productOffers, Offer offer) {
        Map<String, String> bankCodeMap = new HashMap<>();
        Map<Integer, Integer> tenuresCodeMap = new HashMap<>();
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = schemeConfigBO.constructProviderMasterConfigMap(PartnerCodeEnum.payment.getPartnerCode());
        try {
            for (String bankCode : CardTypeEnum.CREDIT.name().equals(offer.getCardType()) ? productOffers.getCreditCard() : productOffers.getPreApprovedCard()) {
                DecimalFormat df = new DecimalFormat("0.00");
                float discount = 0.0f;
                float splitAmount = 0.0f;
                float cFee = 0.0f;
                float processingFeeRate = 0.0f;
                float maxProcessingFee = 0.0f;
                String ccieName = "ccieName";
                PriceResponse pr;
                for (Integer tenure : productOffers.getTenures()) {
                    pr = null;
                    Input input = Input.builder()
                            .cardType(offer.getCardType())
                            .bankCode(bankCode)
                            .tenure(tenure)
                            .offers(null)
                            .brandSubventions(getOfferResponseByOfferId(offer))
                            .convFeeRates(null)
                            .productId(null)
                            .brandProductId(productOffers.getVariant().getBrandProductId())
                            .txnAmount(productOffers.getVariant().getAmount())
                            .merchantId(null)
                            .isSubvented(true)
                            .build();
                    Float cashbackRate = cashbackBO.isEmiCashbackApplicable(productOffers.getVariant().getBrandId(),
                            bankCode, true) ? cashbackBO.calculate(input).getCashback() : 0.0f;
                    float cashback = Float.parseFloat(df.format(productOffers.getVariant().getAmount() * cashbackRate));

                    Float irrpa = null;
                    InterestPerTenureDto interestPerTenureDto = schemeConfigBO.getStandardInterestPerTenure(offer.getCardType(), bankCode, tenure, providerMasterConfigInfoMap);
                    if (Objects.nonNull(interestPerTenureDto) && Objects.nonNull(interestPerTenureDto.getIrr())) {
                        irrpa = interestPerTenureDto.getIrr().floatValue();
                    }
                    if (null != irrpa) {
                        pr = paymentOptionsBO.getPgPriceResponse(offer.getCardType(), bankCode,
                                tenure, discount, productOffers.getVariant().getAmount(),
                                splitAmount, cFee,
                                cashback, null, false, processingFeeRate, maxProcessingFee, ccieName, irrpa, 0, false,
                                null);
                        if (null != pr && isNCE(pr)) {
                            bankCodeMap.put(bankCode, bankCode);
                            tenuresCodeMap.put(tenure, tenure);
                        }
                    }
                }
            }
            if (bankCodeMap.size() > 0 && tenuresCodeMap.size() > 0) {
                if (CardTypeEnum.CREDIT.name().equals(offer.getCardType())) {
                    productOffers.setCreditCard(new ArrayList<>(bankCodeMap.values()));
                } else {
                    productOffers.setPreApprovedCard(new ArrayList<>(bankCodeMap.values()));
                }
                productOffers.setTenures(new ArrayList<>(tenuresCodeMap.values()));
            } else {
                productOffers.setCreditCard(new ArrayList<>());
                productOffers.setPreApprovedCard(new ArrayList<>());
                productOffers.setTenures(new ArrayList<>());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
            productOffers.setCreditCard(new ArrayList<>());
            productOffers.setPreApprovedCard(new ArrayList<>());
            productOffers.setTenures(new ArrayList<>(tenuresCodeMap.values()));
        }
        return productOffers;
    }

    private boolean isNCE(PriceResponse pr) {
        Float cashbackDiscount = pr.getCashback() > 0 ? pr.getCashback() : pr.getDiscount();
        if (pr.getBankCharges() - cashbackDiscount == 0) {
            return true;
        }
        return false;
    }

    public List<OfferResponse> getOfferResponseByOfferId(Offer offer) {
        DecimalFormat df = new DecimalFormat("0.000");
        List<OfferResponse> list = new ArrayList<>();
        OfferResponse offerResponse = new OfferResponse(offer.getId().toString(), offer.getTenure(),
                Float.valueOf(df.format(offer.getSubvention())), offer.getIsValid(),
                offer.getCardType(), offer.getProductId(), offer.getProductIds(), offer.getBankCode(),
                offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), offer.getMaxOfferAmount(),
                offer.getOfferPercentage(), offer.getBankPercentShare(), offer.getBankShareAmt(),
                offer.getBrandPercentShare(), offer.getBrandShareAmt(),
                offer.getMaxBankShare(), offer.getMaxBrandShare(),
                offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount());
        offerResponse.setType(offer.getType());
        list.add(offerResponse);

        return list;
    }

    private boolean deactivateProductOffers(String[] brandIds) {
        try {
            List<ProductOffer> productOffersList =
                    productOfferRepository.findByBrandIdAndIsValid(brandIds, true).orElse(null);
            if (!CollectionUtils.isEmpty(productOffersList)) {
                List<ProductOffer> updateProductOffersList = new ArrayList<>();
                for (ProductOffer productOffers : productOffersList) {
                    productOffers.setIsValid(false);
                    updateProductOffersList.add(productOffers);
                }
                productOfferRepository.saveAll(updateProductOffersList);
            }
            List<ProductOfferCard> productOffersCardList =
                    productOfferCardRepository.findByBrandIdAndIsValid(brandIds, true).orElse(null);
            if (!CollectionUtils.isEmpty(productOffersCardList)) {
                List<ProductOfferCard> updateProductOffersCardList = new ArrayList<>();
                for (ProductOfferCard productOfferCard : productOffersCardList) {
                    productOfferCard.setIsValid(false);
                    updateProductOffersCardList.add(productOfferCard);
                }
                productOfferCardRepository.saveAll(updateProductOffersCardList);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }


    public ProductOfferResponseDTO getProductOffers(ProductOfferRequestDTO productOfferRequest) {
        LOGGER.info("Request received for product offers with params : {}", productOfferRequest);
        List<ProductOfferResponse> responseList = new ArrayList<>();
        int totalOfferCount = 0;
        productOfferRequest = checkDefaultFilter(productOfferRequest);
        if (null == productOfferRequest) {
            return ProductOfferResponseDTO.builder()
                    .productOffers(responseList)
                    .totalOfferCount(totalOfferCount)
                    .build();
        }

        Pageable pageable;
        if (null != productOfferRequest.getLimit() && null != productOfferRequest.getOffset() &&
                null != productOfferRequest.getIsHighToLow()) {
            pageable = new OffsetBasedPageRequest(productOfferRequest.getLimit(), productOfferRequest.getOffset(),
                    new Sort(productOfferRequest.getIsHighToLow() ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "maxOfferAmount"));
        } else {
            pageable = new OffsetBasedPageRequest(1000000, 0, new Sort(Sort.Direction.DESC, "maxOfferAmount"));
        }


        List<ProductOfferCard> productOffersList = null;

        if (productOfferRequest.isBrandSubventionModel()) {
            productOffersList =
                    productOfferCardRepository.findByBrandIdAndBankAndCategoryAndIsValid(productOfferRequest.getBrands(),
                                    productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(),
                                    productOfferRequest.getBanks().getDebit(), productOfferRequest.getBanks().getCredit())
                            .orElse(null);

            if (!CollectionUtils.isEmpty(productOffersList)) {
                totalOfferCount = productOffersList.size();
                productOffersList.clear();
            } else {
                return ProductOfferResponseDTO.builder().productOffers(responseList).totalOfferCount(totalOfferCount)
                        .build();
            }
            productOffersList =
                    productOfferCardRepository.findByBrandIdAndBankAndCategoryAndIsValid(productOfferRequest.getBrands(),
                                    productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(),
                                    productOfferRequest.getBanks().getDebit(), productOfferRequest.getBanks().getCredit()
                                    , pageable)
                            .orElse(null);
        } else {
            productOffersList =
                    productOfferCardRepository.findByBrandIdAndBankAndCategoryAndIsValidAndType(productOfferRequest.getBrands(),
                                    productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(),
                                    productOfferRequest.getBanks().getDebit(), productOfferRequest.getBanks().getCredit(), merchantOfferTypes())
                            .orElse(null);

            if (!CollectionUtils.isEmpty(productOffersList)) {
                totalOfferCount = productOffersList.size();
                productOffersList.clear();
            } else {
                return ProductOfferResponseDTO.builder().productOffers(responseList).totalOfferCount(totalOfferCount)
                        .build();
            }

            productOffersList =
                    productOfferCardRepository.findByBrandIdAndBankAndCategoryAndIsValidAndType(productOfferRequest.getBrands(),
                                    productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(),
                                    productOfferRequest.getBanks().getDebit(), productOfferRequest.getBanks().getCredit()
                                    , merchantOfferTypes(), pageable)
                            .orElse(null);
        }

        String partner = productOfferRequest.getPartner();
        productOffersList = productOffersList.stream()
                .filter(p -> (partner == null && p.getPartner() == null) || (partner != null && partner.equals(p.getPartner())))
                .collect(Collectors.toList());

        List<String> merchantSegmentIds = offerBO.getSegmentIdsForMerchant(productOfferRequest.getMerchantId());

        if (!CollectionUtils.isEmpty(productOffersList)) {
            for (ProductOfferCard productOfferCard : productOffersList) {
                if (!StringUtils.isEmpty(productOfferCard.getSegmentId())) {
                    if (!CollectionUtils.isEmpty(merchantSegmentIds) &&
                            merchantSegmentIds.contains(productOfferCard.getSegmentId())) {
                        responseList.add(new ProductOfferResponse(productOfferCard));
                    }
                } else {
                    responseList.add(new ProductOfferResponse(productOfferCard));
                }
            }
        }

        // fetch segment productIds
        List<String> segmentProductIds = getSegmentProductIds(merchantSegmentIds.toArray(new String[0]));
        List<ProductOfferResponse> tempResponseList = new ArrayList<>();
        for (ProductOfferResponse response : responseList) {
            if (StringUtils.isEmpty(response.getSegmentId())) {
                List<ProductOffer> productOffers = productOfferRepository.findByProductOfferCardIdAndIsValid(
                        response.getProductOfferCardId(), true).orElse(new ArrayList<>());
                List<ProductOffer> validProductOffers = productOffers
                        .stream()
                        .filter(o -> o.getVariant() != null && !StringUtils.isEmpty(o.getVariant().getBrandProductId()))
                        .filter(o -> !segmentProductIds.contains(o.getVariant().getBrandProductId()))
                        .filter(o -> CollectionUtils.isEmpty(segmentProductIds) ? StringUtils.isEmpty(o.getSegmentId()) : true)
                        .collect(Collectors.toList());

                if (StringUtils.isEmpty(response.getType()) && "No Cost EMI".equals(response.getEmiType())) {
                    validProductOffers.addAll(productOffers.stream()
                            .filter(o -> o.getVariant() != null && !StringUtils.isEmpty(o.getSegmentId()) &&
                                    segmentProductIds.contains(o.getVariant().getBrandProductId()))
                            .collect(Collectors.toList()));
                }

                if (!CollectionUtils.isEmpty(validProductOffers)) {
                    Set<String> preApprovedBanks = new HashSet<>();
                    Set<String> creditCards = new HashSet<>();
                    Set<Integer> tenures = new HashSet<>();
                    for (ProductOffer productOffer : validProductOffers) {
                        if (!CollectionUtils.isEmpty(productOffer.getPreApprovedCard()))
                            preApprovedBanks.addAll(productOffer.getPreApprovedCard());
                        if (!CollectionUtils.isEmpty(productOffer.getCreditCard()))
                            creditCards.addAll(productOffer.getCreditCard());
                        if (!CollectionUtils.isEmpty(productOffer.getTenures()))
                            tenures.addAll(productOffer.getTenures());
                    }
                    response.setPreApprovedCard(new ArrayList<>(preApprovedBanks));
                    response.setCreditCard(new ArrayList<>(creditCards));
                    response.setTenures(new ArrayList<>(tenures));
                    tempResponseList.add(response);
                }
            } else {
                tempResponseList.add(response);
            }
        }

        responseList = tempResponseList;

        if (null != productOfferRequest.getIsHighToLow() && !productOfferRequest.getIsHighToLow()) {
            responseList = sortProductOfferResponseList(responseList);
        }
        totalOfferCount = responseList.size();
        return ProductOfferResponseDTO.builder().productOffers(responseList).totalOfferCount(totalOfferCount).build();
    }

    public List<String> getSegmentProductIds(String[] segmentIds) {
        Boolean isValid = true;
        Set<String> productIds = new HashSet<>();
        List<ProductOffer> productOffers = productOfferRepository.findBySegmentIdAndValid(segmentIds, isValid).orElse(new ArrayList<>());
        for (ProductOffer productOffer : productOffers) {
            if (Util.isNotNull(productOffer.getVariant()) && !StringUtils.isEmpty(productOffer.getVariant().getBrandProductId())) {
                productIds.add(productOffer.getVariant().getBrandProductId());
            }
        }
        return new ArrayList<>(productIds);
    }


    private List<ProductOfferResponse> sortProductOfferResponseList(List<ProductOfferResponse> responseList) {
        List<ProductOfferResponse> additionalOfferCardList = new ArrayList<>();
        List<ProductOfferResponse> nceOfferCardList = new ArrayList<>();
        try {
            for (ProductOfferResponse productOfferResponse : responseList) {
                if (productOfferResponse.getMaxOfferAmount() == 0) {
                    nceOfferCardList.add(productOfferResponse);
                } else {
                    additionalOfferCardList.add(productOfferResponse);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        additionalOfferCardList.addAll(nceOfferCardList);
        return additionalOfferCardList;
    }

    public List<ProductOfferResponse> getProductOffersByOfferCard(ProductOfferRequestDTO productOfferRequest) {
        LOGGER.info("Request received for product offers by card with params : {}", productOfferRequest);
        List<ProductOfferResponse> responseList = new ArrayList<>();
        if (StringUtils.hasText(productOfferRequest.getProductOfferCardId())) {
            productOfferRequest = checkDefaultFilterForOfferCard(productOfferRequest);
        } else {
            productOfferRequest = checkDefaultFilter(productOfferRequest);
        }
        if (null == productOfferRequest) {
            return responseList;
        }
        Pageable pageable;
        if (null != productOfferRequest.getLimit() && null != productOfferRequest.getOffset() &&
                null != productOfferRequest.getIsHighToLow()) {
            pageable = new OffsetBasedPageRequest(productOfferRequest.getLimit(), productOfferRequest.getOffset(),
                    new Sort(productOfferRequest.getIsHighToLow() ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "maxOfferAmount"));
        } else {
            pageable = new OffsetBasedPageRequest(1000000, 0, new Sort(Sort.Direction.DESC, "maxOfferAmount"));
        }
        List<ProductOffer> productOffersList = null;
        List<ProductOffer> productOffersCountList = null;
        String partner = productOfferRequest.getPartner();
        if (StringUtils.hasText(productOfferRequest.getProductOfferCardId())) {
            ProductOfferCard productOfferCard =
                    productOfferCardRepository.findById(productOfferRequest.getProductOfferCardId()).orElse(null);

            partner = productOfferCard != null ? productOfferCard.getPartner() : partner;

            if (StringUtils.hasText(productOfferRequest.getSearchText())) {
                productOffersCountList =
                        productOfferRepository.findByProductOfferCardId(productOfferRequest.getProductOfferCardId(),
                                productOfferRequest.getSearchText(), productOfferRequest.getSearchText(),
                                productOfferRequest.getSearchText(), productOfferRequest.getCategories(), productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(), productOfferCard.getSegmentId()).orElse(null);
                productOffersList =
                        productOfferRepository.findByProductOfferCardId(productOfferRequest.getProductOfferCardId(),
                                productOfferRequest.getSearchText(), productOfferRequest.getSearchText(),
                                productOfferRequest.getSearchText(), productOfferRequest.getCategories(), productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(), productOfferCard.getSegmentId(), pageable).orElse(null);
            } else {
                productOffersCountList =
                        productOfferRepository.findByProductOfferCardId(productOfferRequest.getProductOfferCardId(),
                                productOfferRequest.getCategories(), productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(), productOfferCard.getSegmentId()).orElse(null);
                productOffersList =
                        productOfferRepository.findByProductOfferCardId(productOfferRequest.getProductOfferCardId(),
                                productOfferRequest.getCategories(), productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(),
                                productOfferCard.getSegmentId(), pageable).orElse(null);
            }
            if (null != productOfferCard && !CollectionUtils.isEmpty(productOffersList) && !CollectionUtils.isEmpty(productOffersCountList)) {
                for (ProductOffer productOffers : productOffersList) {
                    responseList.add(new ProductOfferResponse(productOffers, productOfferCard, productOffersCountList.size()));
                }
            }
        } else if (StringUtils.hasText(productOfferRequest.getSearchText())) {
            if (productOfferRequest.isBrandSubventionModel()) {
                productOffersList = productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValidAndProduct(
                        productOfferRequest.getBrands(), productOfferRequest.getBanks().getCredit(),
                        productOfferRequest.getBanks().getDebit(), productOfferRequest.getCategories(), Boolean.TRUE,
                        productOfferRequest.getSearchText(), productOfferRequest.getSearchText(),
                        productOfferRequest.getSearchText(), Instant.now(), productOfferRequest.getSearchText(), pageable).orElse(null);
            } else {
                productOffersList = productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValidAndProductAndType(
                        productOfferRequest.getBrands(), productOfferRequest.getBanks().getCredit(),
                        productOfferRequest.getBanks().getDebit(), productOfferRequest.getCategories(), Boolean.TRUE,
                        productOfferRequest.getSearchText(), productOfferRequest.getSearchText(),
                        productOfferRequest.getSearchText(), Instant.now(), merchantOfferTypes(), productOfferRequest.getSearchText(), pageable).orElse(null);
            }

            if (!CollectionUtils.isEmpty(productOffersList)) {
                ProductOfferCard productOfferCard = null;
                for (ProductOffer productOffers : productOffersList) {
                    productOfferCard =
                            productOfferCardRepository.findById(productOffers.getProductOfferCardId()).orElse(null);
                    if (null != productOfferCard) {
                        responseList.add(new ProductOfferResponse(productOffers, productOfferCard, productOfferCard.getProductOfferCount()));
                    }
                }
            }
        } else {
            if (productOfferRequest.isBrandSubventionModel()) {
                productOffersCountList =
                        productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValid(productOfferRequest.getBrands(),
                                productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(),
                                productOfferRequest.getCategories(), Boolean.TRUE, Instant.now()).orElse(null);
                productOffersList =
                        productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValid(productOfferRequest.getBrands(),
                                productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(),
                                productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(), pageable).orElse(null);
            } else {
                productOffersCountList =
                        productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValidAndType(productOfferRequest.getBrands(),
                                productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(),
                                productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(), merchantOfferTypes()).orElse(null);
                productOffersList =
                        productOfferRepository.findByBrandIdAndBankAndCategoryAndIsValidAndType(productOfferRequest.getBrands(),
                                productOfferRequest.getBanks().getCredit(), productOfferRequest.getBanks().getDebit(),
                                productOfferRequest.getCategories(), Boolean.TRUE, Instant.now(), merchantOfferTypes(), pageable).orElse(null);
            }

            if (!CollectionUtils.isEmpty(productOffersCountList) && !CollectionUtils.isEmpty(productOffersList)) {
                ProductOfferCard productOfferCard = null;
                for (ProductOffer productOffers : productOffersList) {
                    productOfferCard =
                            productOfferCardRepository.findById(productOffers.getProductOfferCardId()).orElse(null);
                    if (null != productOfferCard) {
                        responseList.add(new ProductOfferResponse(productOffers, productOfferCard, productOffersCountList.size()));
                    }
                }
            }
        }

        String finalPartner = partner;
        responseList = responseList.stream()
                .filter(p -> (finalPartner == null && p.getPartner() == null) || (finalPartner != null && finalPartner.equals(p.getPartner())))
                .collect(Collectors.toList());

        if (null != productOfferRequest.getIsHighToLow() && !productOfferRequest.getIsHighToLow()) {
            responseList = sortProductOfferResponseList(responseList);
        }

        // fetch segmentIds from merchantId
        // fetch segment productIds

        // In the end - filter
        // productOfferCardId - segmentId
        // if segmentId not present
        // offers productId should not be part of segmentProductIds



        if (!StringUtils.isEmpty(productOfferRequest.getMerchantId())) {
            ProductOfferCard offerCard = productOfferCardRepository.findById(productOfferRequest.getProductOfferCardId()).orElse(null);
            if (offerCard != null) {
                String cardSegmentId = offerCard.getSegmentId();
                if (StringUtils.isEmpty(cardSegmentId)) {
                    List<String> segmentIds = offerBO.getSegmentIdsForMerchant(productOfferRequest.getMerchantId());
                    List<String> segmentProductIds = getSegmentProductIds(segmentIds.toArray(new String[segmentIds.size()]));
                    List<ProductOfferResponse> tempResponseList = new ArrayList<>();
                    for (ProductOfferResponse response : responseList) {
                        if (response.getVariant() != null && !segmentProductIds.contains(response.getVariant().getBrandProductId()) &&
                                (CollectionUtils.isEmpty(segmentProductIds) ? StringUtils.isEmpty(response.getSegmentId()) : true)) {
                            tempResponseList.add(response);
                        } else if (!StringUtils.isEmpty(response.getSegmentId()) &&
                                segmentProductIds.contains(response.getVariant().getBrandProductId()) &&
                                StringUtils.isEmpty(offerCard.getType()) &&
                                "No Cost EMI".equals(offerCard.getEmiType())) {
                            tempResponseList.add(response);
                        }
                    }
                    responseList = tempResponseList;
                }
            }
        }
        return responseList;
    }


    private ProductOfferRequestDTO checkDefaultFilter(ProductOfferRequestDTO productOfferRequestDTO) {
        try {
            DefaultFilterDTO defaultFilterDTO = getDefaultFilters(productOfferRequestDTO.getMerchantId());
            if (!defaultFilterDTO.isMerchantOffline()) {
                return null;
            }
            if (StringUtils.hasText(defaultFilterDTO.getPartner())) {
                productOfferRequestDTO.setPartner(defaultFilterDTO.getPartner());
            }
            productOfferRequestDTO.setBrandSubventionModel(defaultFilterDTO.isBrandSubventionModel());
            if (null == productOfferRequestDTO.getBrands() || productOfferRequestDTO.getBrands().length == 0) {
                productOfferRequestDTO.setBrands(
                        brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getBrandIdsMap().values())));
            }
            if (null == productOfferRequestDTO.getCategories() || productOfferRequestDTO.getCategories().length == 0) {
                productOfferRequestDTO.setCategories(
                        brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getProductCategoryMap().values())));
            }
            if (null == productOfferRequestDTO.getBanks() || ((null == productOfferRequestDTO.getBanks().getCredit() ||
                    productOfferRequestDTO.getBanks().getCredit().length == 0) &&
                    (null == productOfferRequestDTO.getBanks().getDebit() ||
                            productOfferRequestDTO.getBanks().getDebit().length == 0))) {
                productOfferRequestDTO.setBanks(OfferBankRequest.builder()
                        .credit(brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getCcMap().values())))
                        .debit(brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getDcMap().values())))
                        .build());
            }
        } catch (Exception e) {
            LOGGER.info("Exception :{}", e.getMessage());
        }
        return productOfferRequestDTO;
    }

    private DefaultFilterDTO getDefaultFilters(String merchantId) {
        DefaultFilterDTO defaultFilterDTO = new DefaultFilterDTO();
        List<Brand> brandList;
        List<PaymentProviderInfo> merchantProviders = null;
        List<ProductOfferCard> productOffersList = null;
        if (StringUtils.hasText(merchantId)) {
            MerchantUser user = merchantUserBO.getUserById(merchantId);
            defaultFilterDTO.setMerchantOffline("offline".equals(user.getType()));
            defaultFilterDTO.setPartner(user.getPartner());
            brandList = brandBO.findByBrandId(brandBO.getMerchantBrandIds(user));
            merchantProviders = paymentServiceBO.getProvidersInfo(merchantId);
            if (null == merchantProviders || merchantProviders.size() == 0) {
                return defaultFilterDTO;
            }
            defaultFilterDTO.setBrandSubventionModel(user.getIsBrandSubventionModel());
            String[] brandIds = brandBO.getBrandIdByBrands(brandList);
            if (!defaultFilterDTO.isBrandSubventionModel()) {
                productOffersList =
                        productOfferCardRepository.findByBrandIdAndIsValidAndTypeAndPartner(brandIds, true, Instant.now(), merchantOfferTypes(),user.getPartner()).orElse(null);
            } else {
                productOffersList =
                        productOfferCardRepository.findByBrandIdAndIsValidAndPartner(brandIds, true, Instant.now(),user.getPartner()).orElse(null);
            }

            if (!CollectionUtils.isEmpty(productOffersList)) {
                for (ProductOfferCard productOfferCard : productOffersList) {
                    defaultFilterDTO.getBrandIdsMap()
                            .put(productOfferCard.getVariant().getBrandId(), productOfferCard.getVariant().getBrandId());
                    defaultFilterDTO.getProductCategoryMap()
                            .put(productOfferCard.getVariant().getCategory(), productOfferCard.getVariant().getCategory());
                    if (!CollectionUtils.isEmpty(productOfferCard.getCreditCard())) {
                        productOfferCard.getCreditCard().forEach((x) -> defaultFilterDTO.getCcMap().put(x, x));
                    }
                    if (!CollectionUtils.isEmpty(productOfferCard.getPreApprovedCard())) {
                        productOfferCard.getPreApprovedCard().forEach((x) -> defaultFilterDTO.getDcMap().put(x, x));
                    }
                }
                if (StringUtils.hasText(merchantId)) {
                    if (defaultFilterDTO.getDcMap().size() > 0) {
                        for (String provider : new ArrayList<>(defaultFilterDTO.getDcMap().keySet())) {
                            if (!isISGProviderForDebit(provider, "DEBIT", merchantProviders)) {
                                defaultFilterDTO.getDcMap().remove(provider);
                            }
                        }
                    }
                    if (defaultFilterDTO.getCcMap().size() > 0) {
                        for (String provider : new ArrayList<>(defaultFilterDTO.getCcMap().keySet())) {
                            if (!isProviderForCredit(provider, "CREDIT", merchantProviders)) {
                                defaultFilterDTO.getCcMap().remove(provider);
                            }
                        }
                    }
                }
            }
        }
        else {
            defaultFilterDTO.setMerchantOffline(true);
            defaultFilterDTO.setBrandSubventionModel(true);
            List<BrandProductMetaData> brandProductMetaDataList = brandProductMetaDataRepository.findByKeyAndIsValid("All_Brands_Data", Boolean.TRUE).orElse(null);
            if(Util.isNotNull(brandProductMetaDataList)) {
                BrandProductMetaData brandProductMetaData = brandProductMetaDataList.get(0);
                if (!CollectionUtils.isEmpty(brandProductMetaData.getBrandIds())) {
                    brandProductMetaData.getBrandIds().forEach((x) -> defaultFilterDTO.getBrandIdsMap().put(x, x));
                }
                if (!CollectionUtils.isEmpty(brandProductMetaData.getCategories())) {
                    brandProductMetaData.getCategories().forEach((x) -> defaultFilterDTO.getProductCategoryMap().put(x, x));
                }
                if (!CollectionUtils.isEmpty(brandProductMetaData.getCcCards())) {
                    brandProductMetaData.getCcCards().forEach((x) -> defaultFilterDTO.getCcMap().put(x, x));
                }
                if (!CollectionUtils.isEmpty(brandProductMetaData.getDcCards())) {
                    brandProductMetaData.getDcCards().forEach((x) -> defaultFilterDTO.getDcMap().put(x, x));
                }
            }
        }
        return defaultFilterDTO;
    }

    public Boolean isProviderForCredit(String bankCode, String cardType, List<PaymentProviderInfo> providers) {
        if (!cardType.equals("CREDIT")) return true;
        for (PaymentProviderInfo providerInfo : providers) {
            if ((null == providerInfo.getBank() || providerInfo.getBank().getCode().equals(bankCode)) &&
                    (null == providerInfo.getType() || providerInfo.getType().name().equals(cardType)) && (
                    providerInfo.getProvider() == PaymentProviderEnum.isgpg ||
                            providerInfo.getProvider() == PaymentProviderEnum.juspay ||
                            providerInfo.getProvider() == PaymentProviderEnum.tpslemipg ||
                            providerInfo.getProvider() == PaymentProviderEnum.hdfcpg ||
                            providerInfo.getProvider() == PaymentProviderEnum.razorpayemipg ||
                            providerInfo.getProvider() == PaymentProviderEnum.easebuzzpg ||
                            PaymentProviderEnum.lyrapg == providerInfo.getProvider() ||
                            PaymentProviderEnum.ccavenuepg == providerInfo.getProvider() ||
                            PaymentProviderEnum.ccavenueemipg == providerInfo.getProvider() ||
                            PaymentProviderEnum.cashfreeemipg == providerInfo.getProvider() ||
                            PaymentProviderEnum.cashfreepg == providerInfo.getProvider()) &&
                    !providerInfo.getDisabled()) {
                return true;
            }
        }
        return false;
    }

    public Boolean isISGProviderForDebit(String bankCode, String cardType, List<PaymentProviderInfo> providers) {
        for (PaymentProviderInfo providerInfo : providers) {
            if ((null == providerInfo.getBank() || providerInfo.getBank().getCode().equals(bankCode)) &&
                    (null == providerInfo.getType() || providerInfo.getType().name().equals(cardType)) &&
                    !providerInfo.getDisabled()) {
                return true;
            }
        }
        return false;
    }

    public String isOfferFilterEnabled(MerchantUser merchantUser) {
        try {
            if (null != merchantUser.getParams().getIsPreCheckoutOfferEnabled() &&
                    merchantUser.getParams().getIsPreCheckoutOfferEnabled()) {
                ProductOfferRequestDTO productOfferRequestDTO = new ProductOfferRequestDTO();
                productOfferRequestDTO.setMerchantId(merchantUser.getId().toString());
                ProductOfferResponseDTO productOfferResponse = getProductOffers(productOfferRequestDTO);
                if (null != productOfferResponse && productOfferResponse.getTotalOfferCount() > 0) {
                    return "true";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "false";
    }

    private String[] merchantOfferTypes() {
        String[] types = new String[1];
        types[0] = "brandBankAdditionalCashback";
        return types;
    }

    private List<String> getCreditCardBanks() {
        List<String> bankList = new ArrayList<>();
        for (BankEnum bankEnum : BankEnum.values()) {
            if (!StringUtils.hasText(bankEnum.getDebitEmiNumber()) &&
                    !bankEnum.getCode().equals("IIFL") &&
                    !bankEnum.getCode().equals("AUFB") &&
                    !bankEnum.getCode().equals("SBIN") &&
                    !bankEnum.getCode().equals("BNPL_HDFC") &&
                    !bankEnum.getCode().startsWith("CL_") &&
                    !bankEnum.getCode().endsWith("_KYC")) {
                bankList.add(bankEnum.getCode());
            }
        }
        return bankList;
    }


    public void updateProductOffersV2(String brandId, String authCode) {
        LOGGER.info("Request received for update product offers with brandId : {}, authCode : {}", brandId, authCode);
        long start = System.currentTimeMillis();
        if (StringUtils.hasText(authCode) && "ShOpEsE123112200123".equals(authCode)) {
            brandBO.setIsOfferAvailable(brandId);
            String[] brandIds = brandBO.getAllBrandIds(brandId);
            boolean deactivate = deactivateProductOffers(brandIds);
            if (!deactivate) {
                LOGGER.error("Not able to deactivate existing product offers");
                return;
            }
            // non partner cards
            updateProductOffersForPartner(brandIds, null);
            List<Partner> partners = partnerRepository.findAll();
            // partner cards
            if(!CollectionUtils.isEmpty(partners)) {
                for (Partner partner: partners) {
                    if(StringUtils.hasText(partner.getName())) {
                        updateProductOffersForPartner(brandIds, partner.getName());
                    }
                }
            }
            setBrandProductMetaData();
        }
        LOGGER.info("Total processing time is: {}", System.currentTimeMillis() - start);
    }

    public void setBrandProductMetaData(){

        List<BrandProductMetaData> brandProductMetaDataOld=brandProductMetaDataRepository.findByKeyAndIsValid("All_Brands_Data",Boolean.TRUE).orElse(null);
        if(Util.isNotNull(brandProductMetaDataOld)){
            for(BrandProductMetaData brandProductMetaData : brandProductMetaDataOld){
                brandProductMetaData.setIsValid(Boolean.FALSE);
            }
        }
        brandProductMetaDataRepository.saveAll(brandProductMetaDataOld);

        List<Brand> brandList = brandBO.getAllBrands();
        String[] brandIds = brandBO.getBrandIdByBrands(brandList);
        List<ProductOfferCard> productOffersList = productOfferCardRepository.findByBrandIdAndIsValid(brandIds, true, Instant.now()).orElse(null);

        List<String> brandIdsList = new ArrayList();
        List<String> categoryList = new ArrayList<>();
        List<String> ccCardsList = new ArrayList<>();
        List<String> dcCardsList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(productOffersList)) {
            for (ProductOfferCard productOfferCard : productOffersList) {
                brandIdsList.add(productOfferCard.getVariant().getBrandId());
                categoryList.add(productOfferCard.getVariant().getCategory());
                if (!CollectionUtils.isEmpty(productOfferCard.getCreditCard())) {
                    productOfferCard.getCreditCard().forEach((x) -> ccCardsList.add(x));
                }
                if (!CollectionUtils.isEmpty(productOfferCard.getPreApprovedCard())) {
                    productOfferCard.getPreApprovedCard().forEach((x) -> dcCardsList.add(x));
                }
            }
        }
        BrandProductMetaData brandProductMetaDataNew = BrandProductMetaData.builder().key("All_Brands_Data").brandIds(brandIdsList).categories(categoryList).ccCards(ccCardsList).dcCards(dcCardsList).isValid(Boolean.TRUE).build();
        brandProductMetaDataRepository.save(brandProductMetaDataNew);
    }

    private void updateProductOffersForPartner(String[] brandIds, String partner) {
        Map<String, List<ProductOffersCardsDTO>> brandProductsOfferCardMap = new HashMap<>();
        try {
            List<String> defaultCreditCardBanks = getCreditCardBanks();
            List<Offer> offerList = null;
            if (null != brandIds && brandIds.length > 0) {
                offerList = offerBO.findByBrandIds(brandIds, partner);
                if (!CollectionUtils.isEmpty(offerList)) {
                    List<BrandProduct> brandProductList;
                    Map<String, Brand> brandMap = brandBO.getBrandMap();
                    List<ProductOffer> offerProductsList;
                    for (Offer offer : offerList) {
                        if (brandMap.containsKey(offer.getBrandId()) && (null != offer.getTenure() || StringUtils.hasText(offer.getType())) && (Util.isNotNull(offer.getIsValid()) && offer.getIsValid())) {
                            offerProductsList = new ArrayList<>();
                            if (StringUtils.hasText(offer.getProductId()) && "any".equals(offer.getProductId())) {
                                brandProductList = brandProductBO.findByBrandId(offer.getBrandId());
                                for (BrandProduct brandProduct : brandProductList) {
                                    offerProductsList =
                                            getProductOffer(brandMap.get(brandProduct.getBrandId()), brandProduct, offer,
                                                    offerProductsList, defaultCreditCardBanks);
                                }
                            } else if (!CollectionUtils.isEmpty(offer.getProductIds())) {
                                brandProductList = brandProductBO.findByIds(offer.getProductIds());
                                for (BrandProduct brandProduct : brandProductList) {
                                    offerProductsList =
                                            getProductOffer(brandMap.get(brandProduct.getBrandId()), brandProduct, offer,
                                                    offerProductsList, defaultCreditCardBanks);
                                }
                            } else if (StringUtils.hasText(offer.getProductId())) {
                                BrandProduct brandProduct = brandProductBO.getById(offer.getProductId());
                                offerProductsList =
                                        getProductOffer(brandMap.get(brandProduct.getBrandId()), brandProduct, offer,
                                                offerProductsList, defaultCreditCardBanks);
                            }
                            if (!CollectionUtils.isEmpty(offerProductsList)) {
                                brandProductsOfferCardMap = getProductOfferCardV2(brandProductsOfferCardMap, offerProductsList);
                            }
                        } else {
                            LOGGER.info("Invalid offer found with id  :{}, tenure :{}, type : {},  status:{}, brand id : {} ",
                                    offer.getId(), offer.getTenure(), offer.getType(), offer.getIsValid(), offer.getBrandId());
                        }
                    }
                    if (brandProductsOfferCardMap.size() > 0) {
                        Map<String, List<ProductOffersCardsDTO>> clubBrandProductsOfferCardMap = new HashMap<>();
                        for (String brand : brandProductsOfferCardMap.keySet()) {
                            clubBrandProductsOfferCardMap.put(brand, mergeNceAndAdditionalOfferCard(brandProductsOfferCardMap.get(brand)));
                        }
                        List<ProductOffer> productOffersBachList = new ArrayList<>();
                        for (String brand : clubBrandProductsOfferCardMap.keySet()) {
                            List<ProductOffer> productOfferList = null;
                            for (ProductOffersCardsDTO productOffersCardsDTO : clubBrandProductsOfferCardMap.get(brand)) {
                                productOfferList = mergeVariantBank(productOffersCardsDTO.getProductOfferList());
                                productOfferList = mergeVariantTenure(productOfferList);
                                if (!CollectionUtils.isEmpty(productOfferList)) {
                                    ProductOfferCard productOfferCard = new ProductOfferCard();
                                    productOfferCard.setProductOfferCount(productOfferList.size());
                                    productOfferCard.setCreditCard(
                                            new ArrayList<>(productOffersCardsDTO.getCreditCards().values()));
                                    productOfferCard.setPreApprovedCard(
                                            new ArrayList<>(productOffersCardsDTO.getBanks().values()));
                                    productOfferCard.setTenures(new ArrayList<>(productOffersCardsDTO.getTenures().values()));
                                    productOfferCard.setOfferPercentage(productOffersCardsDTO.getOfferPercentage());
                                    productOfferCard.setCashback(productOffersCardsDTO.getCashback());
                                    productOfferCard.setMaxOfferAmount(productOffersCardsDTO.getMaxOfferAmount());
                                    productOfferCard.setOfferPercentage(productOffersCardsDTO.getOfferPercentage());
                                    productOfferCard.setType(productOffersCardsDTO.getType());
                                    productOfferCard.setEmiType(productOffersCardsDTO.getEmiType());
                                    productOfferCard.setIsValid(true);
                                    productOfferCard.setVariant(productOfferList.get(0).getVariant());
                                    productOfferCard.setValidFrom(productOfferList.get(0).getValidFrom());
                                    productOfferCard.setValidTo(productOfferList.get(0).getValidTo());
                                    productOfferCard.setSubvention(productOffersCardsDTO.getSubvention());
                                    productOfferCard.setOfferDescription(productOfferList.get(0).getOfferDescription());

                                    if (!(StringUtils.isEmpty(productOfferCard.getType()) && "No Cost EMI".equals(productOfferCard.getEmiType()))) {
                                        for (ProductOffer productOffer: productOfferList) {
                                            productOfferCard.setSegmentId(productOffer.getSegmentId());
                                        }
                                    }
                                    productOfferCard.setPartner(partner);
                                    productOfferCardRepository.save(productOfferCard);
                                    for (ProductOffer productOffer : productOfferList) {
                                        productOffer.setProductOfferCardId(productOfferCard.getId().toString());
                                        productOffer.setEmiType(productOfferCard.getEmiType());
                                        productOffer.setPartner(partner);
                                        productOffersBachList.add(productOffer);
                                    }
                                    LOGGER.info("Offer card id : {} total offers : {}", productOfferCard.getId().toString(), productOffersBachList.size());
                                    productOfferRepository.saveAll(productOffersBachList);
                                    productOffersBachList.clear();
                                    productOfferList.clear();
                                }
                            }
                        }
                    }

                } else {
                    LOGGER.info("No offers found");
                }
            } else {
                LOGGER.info("No brands found");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
    }

    private Map<String, List<ProductOffersCardsDTO>> getProductOfferCardV2(
            Map<String, List<ProductOffersCardsDTO>> productOfferMap, List<ProductOffer> productOffers) {
        try {
            if (productOfferMap.size() == 0 || !productOfferMap.containsKey(productOffers.get(0).getVariant().getBrandId())) {
                productOfferMap.put(productOffers.get(0).getVariant().getBrandId(), new ArrayList<>());
                ProductOffersCardsDTO productOffersCardDTO = new ProductOffersCardsDTO(productOffers.get(0));
                productOffersCardDTO.getOfferIds().add(productOffers.get(0).getOfferId());
                productOfferMap.get(productOffers.get(0).getVariant().getBrandId())
                        .add(updateProductOffersCardsDTO(productOffersCardDTO, productOffers));
            } else if (productOfferMap.containsKey(productOffers.get(0).getVariant().getBrandId())) {
                ProductOffersCardsDTO productOffersCardDTO = new ProductOffersCardsDTO(productOffers.get(0));
                productOffersCardDTO.getOfferIds().add(productOffers.get(0).getOfferId());
                productOfferMap.get(productOffers.get(0).getVariant().getBrandId())
                        .add(updateProductOffersCardsDTO(productOffersCardDTO, productOffers));
            } else {
                productOfferMap.put(productOffers.get(0).getVariant().getBrandId(), new ArrayList<>());
                ProductOffersCardsDTO productOffersCardDTO = new ProductOffersCardsDTO(productOffers.get(0));
                productOffersCardDTO.getOfferIds().add(productOffers.get(0).getOfferId());
                productOfferMap.get(productOffers.get(0).getVariant().getBrandId())
                        .add(updateProductOffersCardsDTO(productOffersCardDTO, productOffers));
            }

        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return productOfferMap;
    }

    private ProductOffersCardsDTO updateProductOffersCardsDTO(ProductOffersCardsDTO productOffersCardDTO,
                                                              List<ProductOffer> productOffers) {
        try {
            for (Integer tenure : productOffers.get(0).getTenures()) {
                productOffersCardDTO.getTenures().put(tenure, tenure);
            }
            if (!CollectionUtils.isEmpty(productOffers.get(0).getPreApprovedCard())) {
                productOffers.get(0).getPreApprovedCard().forEach(x -> productOffersCardDTO.getBanks().put(x, x));
            }
            if (!CollectionUtils.isEmpty(productOffers.get(0).getCreditCard())) {
                productOffers.get(0).getCreditCard().forEach(x -> productOffersCardDTO.getCreditCards().put(x, x));
            }
            for (ProductOffer productOffer : productOffers) {
                productOffersCardDTO.getProducts().put(productOffer.getVariant().getBrandProductId(), productOffer);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return productOffersCardDTO;
    }


    private List<ProductOffersCardsDTO> mergeNceAndAdditionalOfferCard(List<ProductOffersCardsDTO> productOfferCardList) {
        List<ProductOffersCardsDTO> clubOfferCardList = new ArrayList<>();
        try {
            List<ProductOffersCardsDTO> additionalOfferCardList = new ArrayList<>();
            ProductOffersCardsDTO finalNceProductOffersCardsDTO = null;
            for (ProductOffersCardsDTO productOffersCardsDTO : productOfferCardList) {
                if ("brandBankAdditionalCashback".equals(productOffersCardsDTO.getType())) {
                    productOffersCardsDTO.getProductOfferList().addAll(productOffersCardsDTO.getProducts().values());
                    additionalOfferCardList.add(productOffersCardsDTO);
                } else if ("No Cost EMI".equals(productOffersCardsDTO.getEmiType())) {
                    if (null == finalNceProductOffersCardsDTO) {
                        finalNceProductOffersCardsDTO = productOffersCardsDTO;
                        finalNceProductOffersCardsDTO.getProductOfferList().addAll(productOffersCardsDTO.getProducts().values());
                    } else {
                        finalNceProductOffersCardsDTO.getProductOfferList().addAll(productOffersCardsDTO.getProducts().values());
                        for (ProductOffer productOffer : productOffersCardsDTO.getProducts().values()) {
                            finalNceProductOffersCardsDTO.getProducts().put(productOffer.getVariant().getBrandProductId(), productOffer);
                        }
                    }
                    finalNceProductOffersCardsDTO = addBanks(finalNceProductOffersCardsDTO, productOffersCardsDTO);
                    finalNceProductOffersCardsDTO = addTenures(finalNceProductOffersCardsDTO, productOffersCardsDTO);
                }
            }
            if (null != finalNceProductOffersCardsDTO && finalNceProductOffersCardsDTO.getProducts().size() > 0) {
                ProductOffersCardsDTO clubCard = null;
                for (ProductOffersCardsDTO additionalProductOffersCardsDTO : additionalOfferCardList) {
                    if (isContainAllNCEBank(finalNceProductOffersCardsDTO, additionalProductOffersCardsDTO)) {
                        clubCard = getClubCard(additionalProductOffersCardsDTO);
                        for (String brandProductId : additionalProductOffersCardsDTO.getProducts().keySet()) {
                            if (finalNceProductOffersCardsDTO.getProducts().containsKey(brandProductId)) {
                                additionalProductOffersCardsDTO.getClubProducts().put(brandProductId, brandProductId);
                                clubCard.getProducts().put(brandProductId, additionalProductOffersCardsDTO.getProducts().get(brandProductId));
                                clubCard.setEmiType(finalNceProductOffersCardsDTO.getEmiType());
                            }
                        }
                        if (null != clubCard && clubCard.getProducts().size() > 0) {
                            clubOfferCardList.add(clubCard);
                            if (clubCard.getProducts().size() != additionalProductOffersCardsDTO.getProducts().size()) {
                                clubOfferCardList.add(additionalProductOffersCardsDTO);
                            }
                            clubCard = null;
                        } else {
                            clubOfferCardList.add(additionalProductOffersCardsDTO);
                        }
                    } else {
                        clubOfferCardList.add(additionalProductOffersCardsDTO);
                    }
                }
                additionalOfferCardList = new ArrayList<>();
                additionalOfferCardList.addAll(clubOfferCardList);
                clubOfferCardList = new ArrayList<>();
            }
            for (ProductOffersCardsDTO additionalProductOffersCardsDTO : additionalOfferCardList) {
                if (additionalProductOffersCardsDTO.getClubProducts().size() > 0 &&
                        additionalProductOffersCardsDTO.getTenures().size() == 1 &&
                        (additionalProductOffersCardsDTO.getCreditCards().size() <= 1 && additionalProductOffersCardsDTO.getBanks().size() <= 1)
                ) {
                    for (String brandProductId : additionalProductOffersCardsDTO.getClubProducts().keySet()) {
                        additionalProductOffersCardsDTO.getProducts().remove(brandProductId);
                    }
                    if (additionalProductOffersCardsDTO.getProducts().size() > 0) {
                        additionalProductOffersCardsDTO.getProductOfferList().clear();
                        additionalProductOffersCardsDTO.getProductOfferList().addAll(additionalProductOffersCardsDTO.getProducts().values());
                        clubOfferCardList.add(additionalProductOffersCardsDTO);
                    }
                } else {
                    clubOfferCardList.add(additionalProductOffersCardsDTO);
                }
            }
            clubOfferCardList = checkSameOffer(clubOfferCardList);
            if (null != finalNceProductOffersCardsDTO) {
                clubOfferCardList.add(finalNceProductOffersCardsDTO);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return clubOfferCardList;
    }

    ProductOffersCardsDTO getClubCard(ProductOffersCardsDTO additionalProductOffersCardsDTO) {
        List<ProductOffer> cardProductList = new ArrayList<>(additionalProductOffersCardsDTO.getProducts().values());
        ProductOffersCardsDTO clubCard = new ProductOffersCardsDTO(cardProductList.get(0));
        clubCard.setBanks(additionalProductOffersCardsDTO.getBanks());
        clubCard.setCreditCards(additionalProductOffersCardsDTO.getCreditCards());
        clubCard.setOfferIds(additionalProductOffersCardsDTO.getOfferIds());
        return clubCard;
    }

    private List<ProductOffersCardsDTO> checkSameOffer(List<ProductOffersCardsDTO> productOfferCardList) {
        List<ProductOffersCardsDTO> clubOfferCardList = new ArrayList<>();
        try {
            for (ProductOffersCardsDTO productOffersCardsDTO : productOfferCardList) {
                clubOfferCardList = mergeCards(clubOfferCardList, productOffersCardsDTO);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return clubOfferCardList;
    }

    private List<ProductOffersCardsDTO> mergeCards(List<ProductOffersCardsDTO> productOfferCardList, ProductOffersCardsDTO productOffersCardDTO) {
        List<ProductOffersCardsDTO> updatedProductOfferCardList = new ArrayList<>();
        try {
            if (CollectionUtils.isEmpty(productOfferCardList)) {
                productOffersCardDTO.getProductOfferList().addAll(productOffersCardDTO.getProducts().values());
                updatedProductOfferCardList.add(productOffersCardDTO);
            } else {
                boolean isCardsMerged = false;
                for (ProductOffersCardsDTO productOffersCard : productOfferCardList) {
                    if ((!StringUtils.hasText(productOffersCard.getEmiType()) &&
                            !StringUtils.hasText(productOffersCardDTO.getEmiType())
                            || (StringUtils.hasText(productOffersCard.getEmiType()) &&
                            productOffersCard.getEmiType().equals(productOffersCardDTO.getEmiType()))) &&
                            StringUtils.hasText(productOffersCard.getType()) && productOffersCard.getType().equals(productOffersCardDTO.getType()) &&
                            productOffersCard.getOfferPercentage().equals(productOffersCardDTO.getOfferPercentage()) &&
                            ((StringUtils.isEmpty(productOffersCard.getSegmentId()) &&
                                    StringUtils.isEmpty(productOffersCardDTO.getSegmentId()))
                                    || (StringUtils.hasText(productOffersCard.getSegmentId()) &&
                                    productOffersCard.getSegmentId().equals(productOffersCardDTO.getSegmentId())))) {
                        productOffersCard = addBanks(productOffersCard, productOffersCardDTO);
                        productOffersCard.getProductOfferList().addAll(productOffersCardDTO.getProducts().values());
                        if (productOffersCardDTO.getMaxOfferAmount() != null) {
                            if (productOffersCard.getMaxOfferAmount() == null ||
                                    productOffersCardDTO.getMaxOfferAmount() > productOffersCard.getMaxOfferAmount()) {
                                productOffersCard.setMaxOfferAmount(productOffersCardDTO.getMaxOfferAmount());
                            }
                        }
                        isCardsMerged = true;
                    }
                    updatedProductOfferCardList.add(productOffersCard);
                }
                if (!isCardsMerged) {
                    productOffersCardDTO.getProductOfferList().addAll(productOffersCardDTO.getProducts().values());
                    updatedProductOfferCardList.add(productOffersCardDTO);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return updatedProductOfferCardList;
    }

    private ProductOffersCardsDTO addBanks(ProductOffersCardsDTO productOffersCard1, ProductOffersCardsDTO productOffersCard2) {
        if (productOffersCard2.getBanks().size() > 0) {
            for (String preApprovedBank : productOffersCard2.getBanks().values()) {
                productOffersCard1.getBanks().put(preApprovedBank, preApprovedBank);
            }
        }
        if (productOffersCard2.getCreditCards().size() > 0) {
            for (String preApprovedBank : productOffersCard2.getCreditCards().values()) {
                productOffersCard1.getCreditCards().put(preApprovedBank, preApprovedBank);
            }
        }
        return productOffersCard1;
    }

    private ProductOffersCardsDTO addTenures(ProductOffersCardsDTO productOffersCard1, ProductOffersCardsDTO productOffersCard2) {
        if (productOffersCard2.getTenures().size() > 0) {
            for (Integer tenure : productOffersCard2.getTenures().values()) {
                productOffersCard1.getTenures().put(tenure, tenure);
            }
        }
        return productOffersCard1;
    }


    public OfferFiltersResponse getProductOffersFilterByOfferCard(String offerCardId) {
        LOGGER.info("Received request for filers for offerCardId : {}", offerCardId);
        OfferFiltersResponse offerFiltersResponse = new OfferFiltersResponse();
        try {
            DefaultFilterDTO defaultFilterDTO = getDefaultFilerByOfferCard(offerCardId);
            if (null != defaultFilterDTO) {
                offerFiltersResponse.setCategories(defaultFilterDTO.getProductCategoryMap()
                        .values()
                        .stream()
                        .map((x) -> ProductCategory.builder().name(x).build())
                        .collect(Collectors.toList()));
                offerFiltersResponse.setBanks(OfferBankDTO.builder()
                        .credit(new ArrayList<>(defaultFilterDTO.getCcMap().values()))
                        .debit(new ArrayList<>(defaultFilterDTO.getDcMap().values()))
                        .build());
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return offerFiltersResponse;
    }

    private DefaultFilterDTO getDefaultFilerByOfferCard(String offerCardId) {
        DefaultFilterDTO defaultFilterDTO = new DefaultFilterDTO();
        try {
            List<ProductOffer> productOffersList =
                    productOfferRepository.findByProductOfferCardId(offerCardId).orElse(null);
            if (!CollectionUtils.isEmpty(productOffersList)) {
                for (ProductOffer productOffers : productOffersList) {
                    defaultFilterDTO.getProductCategoryMap().put(productOffers.getVariant().getCategory(), productOffers.getVariant().getCategory());

                    if (!CollectionUtils.isEmpty(productOffers.getPreApprovedCard())) {
                        productOffers.getPreApprovedCard().forEach(x -> defaultFilterDTO.getDcMap().put(x, x));
                    }
                    if (!CollectionUtils.isEmpty(productOffers.getCreditCard())) {
                        productOffers.getCreditCard().forEach(x -> defaultFilterDTO.getCcMap().put(x, x));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return defaultFilterDTO;
    }

    private ProductOfferRequestDTO checkDefaultFilterForOfferCard(ProductOfferRequestDTO productOfferRequestDTO) {
        try {
            DefaultFilterDTO defaultFilterDTO = getDefaultFilerByOfferCard(productOfferRequestDTO.getProductOfferCardId());
            if (null == productOfferRequestDTO.getCategories() || productOfferRequestDTO.getCategories().length == 0) {
                productOfferRequestDTO.setCategories(
                        brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getProductCategoryMap().values())));
            }
            if (null == productOfferRequestDTO.getBanks() || ((null == productOfferRequestDTO.getBanks().getCredit() ||
                    productOfferRequestDTO.getBanks().getCredit().length == 0) &&
                    (null == productOfferRequestDTO.getBanks().getDebit() ||
                            productOfferRequestDTO.getBanks().getDebit().length == 0))) {
                productOfferRequestDTO.setBanks(OfferBankRequest.builder()
                        .credit(brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getCcMap().values())))
                        .debit(brandBO.getDataArray(new ArrayList<>(defaultFilterDTO.getDcMap().values())))
                        .build());
            }
        } catch (Exception e) {
            LOGGER.info("Exception :{}", e.getMessage());
        }
        return productOfferRequestDTO;
    }

    private List<ProductOffer> mergeVariantTenure(List<ProductOffer> productOffers) {
        ArrayList<ProductOffer> productOfferList = new ArrayList<>();
        Map<String, ArrayList<ProductOffer>> brandProductOffersListMap = new HashMap<>();
        try {
            boolean isVariantOfferEqual;
            for (ProductOffer productOfferVariant : productOffers) {
                productOfferList = new ArrayList<>();
                isVariantOfferEqual = false;
                if (brandProductOffersListMap.containsKey(productOfferVariant.getVariant().getBrandProductId())) {
                    for (ProductOffer offerVariant : brandProductOffersListMap.get(productOfferVariant.getVariant().getBrandProductId())) {
                        if (!isVariantOfferEqual && isTypeEqual(offerVariant, productOfferVariant) && isValidityEqual(offerVariant, productOfferVariant)
                                && isBankEqual(offerVariant, productOfferVariant)) {
                            for (Integer tenure : productOfferVariant.getTenures()) {
                                if (!offerVariant.getTenures().contains(tenure)) {
                                    offerVariant.getTenures().add(tenure);
                                }
                            }
                            isVariantOfferEqual = true;
                        }
                        productOfferList.add(offerVariant);
                    }
                    if (!isVariantOfferEqual) {
                        productOfferList.add(productOfferVariant);
                    }
                    brandProductOffersListMap.put(productOfferVariant.getVariant().getBrandProductId(), productOfferList);
                } else {
                    productOfferList.add(productOfferVariant);
                    brandProductOffersListMap.put(productOfferVariant.getVariant().getBrandProductId(), productOfferList);
                }
            }
            productOfferList = new ArrayList<>();
            for (String brandProductId : brandProductOffersListMap.keySet()) {
                productOfferList.addAll(brandProductOffersListMap.get(brandProductId));
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred : {} ", e.getMessage());
        }
        return productOfferList;
    }

    private List<ProductOffer> mergeVariantBank(List<ProductOffer> productOffers) {
        ArrayList<ProductOffer> productOfferList = new ArrayList<>();
        Map<String, ArrayList<ProductOffer>> brandProductOffersListMap = new HashMap<>();
        try {
            boolean isVariantOfferEqual;
            for (ProductOffer productOfferVariant : productOffers) {
                productOfferList = new ArrayList<>();
                isVariantOfferEqual = false;
                if (brandProductOffersListMap.containsKey(productOfferVariant.getVariant().getBrandProductId())) {
                    for (ProductOffer offerVariant : brandProductOffersListMap.get(productOfferVariant.getVariant().getBrandProductId())) {
                        if (!isVariantOfferEqual && isTypeEqual(offerVariant, productOfferVariant) &&
                                isValidityEqual(offerVariant, productOfferVariant) &&
                                isTenureEqual(offerVariant, productOfferVariant) && ((StringUtils.isEmpty(offerVariant.getSegmentId()) &&
                                StringUtils.isEmpty(productOfferVariant.getSegmentId()))
                                || (StringUtils.hasText(offerVariant.getSegmentId()) &&
                                offerVariant.getSegmentId().equals(productOfferVariant.getSegmentId())))) {
                            if (!CollectionUtils.isEmpty(productOfferVariant.getPreApprovedCard())) {
                                for (String bankCode : productOfferVariant.getPreApprovedCard()) {
                                    if (!offerVariant.getPreApprovedCard().contains(bankCode)) {
                                        offerVariant.getPreApprovedCard().add(bankCode);
                                    }
                                }
                            }
                            if (!CollectionUtils.isEmpty(productOfferVariant.getCreditCard())) {
                                for (String bankCode : productOfferVariant.getCreditCard()) {
                                    if (!offerVariant.getCreditCard().contains(bankCode)) {
                                        offerVariant.getCreditCard().add(bankCode);
                                    }
                                }
                            }
                            isVariantOfferEqual = true;
                        }
                        productOfferList.add(offerVariant);
                    }
                    if (!isVariantOfferEqual) {
                        productOfferList.add(productOfferVariant);
                    }
                    brandProductOffersListMap.put(productOfferVariant.getVariant().getBrandProductId(), productOfferList);
                } else {
                    productOfferList.add(productOfferVariant);
                    brandProductOffersListMap.put(productOfferVariant.getVariant().getBrandProductId(), productOfferList);
                }
            }
            productOfferList = new ArrayList<>();
            for (String brandProductId : brandProductOffersListMap.keySet()) {
                productOfferList.addAll(brandProductOffersListMap.get(brandProductId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Exception occurred : {} ", e.getMessage());
        }
        return productOfferList;
    }

    private boolean isTypeEqual(ProductOffer productOfferVariant1, ProductOffer productOfferVariant2) {
        try {
            if (((productOfferVariant1.getEmiType() == null && productOfferVariant2.getEmiType() == null) || (StringUtils.hasText(productOfferVariant1.getEmiType()) && StringUtils.hasText(productOfferVariant2.getEmiType())
                    && (productOfferVariant1.getEmiType().equals(productOfferVariant2.getEmiType())))))
                return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }

    private boolean isValidityEqual(ProductOffer productOfferVariant1, ProductOffer productOfferVariant2) {
        try {
            if ((null == productOfferVariant1.getValidFrom() && null == productOfferVariant2.getValidFrom()
                    && null == productOfferVariant1.getValidTo() && null == productOfferVariant2.getValidTo())
                    || ((productOfferVariant1.getValidFrom().getEpochSecond() == productOfferVariant2.getValidFrom().getEpochSecond())
                    && (productOfferVariant1.getValidTo().getEpochSecond() == productOfferVariant2.getValidTo().getEpochSecond())))
                return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }

    private boolean isBankEqual(ProductOffer productOfferVariant1, ProductOffer productOfferVariant2) {
        try {
            if (((CollectionUtils.isEmpty(productOfferVariant1.getPreApprovedCard()) && CollectionUtils.isEmpty(productOfferVariant2.getPreApprovedCard()))
                    || (productOfferVariant1.getPreApprovedCard().size() == productOfferVariant2.getPreApprovedCard().size()
                    && productOfferVariant1.getPreApprovedCard().containsAll(productOfferVariant2.getPreApprovedCard())))
                    &&
                    ((CollectionUtils.isEmpty(productOfferVariant1.getCreditCard()) && CollectionUtils.isEmpty(productOfferVariant2.getCreditCard()))
                            || (productOfferVariant1.getCreditCard().size() == productOfferVariant2.getCreditCard().size()
                            && productOfferVariant1.getCreditCard().containsAll(productOfferVariant2.getCreditCard())))
            )
                return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }

    private boolean isContainAllNCEBank(ProductOffersCardsDTO productOfferVariant1, ProductOffersCardsDTO productOfferVariant2) {
        try {
            if (((productOfferVariant1.getBanks().size() == 0 && productOfferVariant2.getBanks().size() == 0) || (
                    productOfferVariant1.getBanks().values().containsAll(productOfferVariant2.getBanks().values())))
                    &&
                    ((null == productOfferVariant1.getCreditCards() && null == productOfferVariant2.getCreditCards()) || (
                            productOfferVariant1.getCreditCards().values().containsAll(productOfferVariant2.getCreditCards().values())))
            )
                return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }

    private boolean isTenureEqual(ProductOffer productOfferVariant1, ProductOffer productOfferVariant2) {
        try {
            if (productOfferVariant1.getTenures().size() == productOfferVariant2.getTenures().size()
                    && productOfferVariant1.getTenures().containsAll(productOfferVariant2.getTenures()))
                return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return false;
    }


    private ProductOffersCardsDTO addBanks2(ProductOffersCardsDTO productOffersCard1, ProductOffersCardsDTO productOffersCard2) {
        if (productOffersCard2.getBanks().size() > 0) {
            for (String preApprovedBank : productOffersCard2.getBanks().values()) {
                productOffersCard1.getBanks().put(preApprovedBank, preApprovedBank);
            }
        }
        if (productOffersCard2.getCreditCards().size() > 0) {
            for (String preApprovedBank : productOffersCard2.getCreditCards().values()) {
                productOffersCard1.getCreditCards().put(preApprovedBank, preApprovedBank);
            }
        }
        return productOffersCard1;
    }
}
