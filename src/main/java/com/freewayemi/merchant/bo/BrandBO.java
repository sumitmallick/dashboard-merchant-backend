package com.freewayemi.merchant.bo;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.freewayemi.merchant.commons.bo.CashbackBO;
import com.freewayemi.merchant.commons.bo.PaymentOptionsBO;
import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.dto.PriceResponse;
import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.BrandInfoRepsonse;
import com.freewayemi.merchant.dto.response.BrandResponse;
import com.freewayemi.merchant.dto.response.ProductCategory;
import com.freewayemi.merchant.dto.response.ProductOfferVariant;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigInfo;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.enums.BrandType;
import com.freewayemi.merchant.repository.BrandRepository;
import com.freewayemi.merchant.repository.BrandsProductRepository;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import com.freewayemi.merchant.repository.PartnerRepository;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.PartnerCodeEnum;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BrandBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandRepository.class);
    private final BrandRepository brandRepository;
    private final Boolean nonPartnerBrandEnabled;
    private final OfferBO offerBO;
    private final MerchantConfigsRepository merchantConfigsRepository;
    private final BrandsProductRepository brandsProductRepository;
    private final PaymentOptionsBO paymentOptionsBO;
    private final CashbackBO cashbackBO;
    private final List<Integer> offerDefaultTenures;
    private final List<String> defaultPreApprovedBanks;
    private final PartnerRepository partnerRepository;
    private SchemeConfigBO schemeConfigBO;

    @Autowired
    public BrandBO(BrandRepository brandRepository, MerchantConfigsRepository merchantConfigsRepository,
                   @Value("${non.partner.brand.enable}") Boolean nonPartnerBrandEnabled,
                   OfferBO offerBO,
                   BrandsProductRepository brandsProductRepository,
                   @Value("${offer.default.tenure}") List<Integer> offerDefaultTenures,
                   @Value("${offer.filter.pre.approved.banks}") List<String> defaultPreApprovedBanks,
                   PaymentOptionsBO paymentOptionsBO, CashbackBO cashbackBO, PartnerRepository partnerRepository, SchemeConfigBO schemeConfigBO) {
        this.brandRepository = brandRepository;
        this.nonPartnerBrandEnabled = nonPartnerBrandEnabled;
        this.offerBO = offerBO;
        this.merchantConfigsRepository = merchantConfigsRepository;
        this.brandsProductRepository = brandsProductRepository;
        this.partnerRepository = partnerRepository;
        this.offerDefaultTenures = offerDefaultTenures;
        this.defaultPreApprovedBanks = defaultPreApprovedBanks;
        this.paymentOptionsBO = paymentOptionsBO;
        this.cashbackBO = cashbackBO;
        this.schemeConfigBO = schemeConfigBO;
    }

    public List<BrandResponse> get(MerchantUser merchantUser, Boolean isExclusion) {
        List<BrandResponse> brands = new ArrayList<>();
        if (!StringUtils.isEmpty(merchantUser)) {
            Params params = merchantUser.getParams();
            List<String> brandIds = Util.getCombinedBrandIds(params.getBrandId(), params.getBrandIds());
            for (String brandId : brandIds) {
                Brand brand = this.findById(brandId);
                if (null != brand && null != brand.getHasProducts() && brand.getHasProducts()) {
                    // Servify brandId will not be included
                    if (Util.isNotNull(isExclusion) && isExclusion && ("63417134435524138fd1c745".equals(brand.getId().toString()))) {
                        continue;
                    }
                    brands.add(new BrandResponse(brand));
                }
            }
        }
        return brands;
    }

    public List<BrandResponse> getBrands(MerchantUser merchantUser) {
        List<BrandResponse> brands = new ArrayList<>();
        if (Objects.nonNull(merchantUser) && !StringUtils.isEmpty(merchantUser)) {
            Params params = merchantUser.getParams();
            String brand_config_name = "brand_list_to_display_products_for_payment_link_generation";
            Optional<MerchantConfigs> optionalMerchantConfigs = merchantConfigsRepository.findByLabel(brand_config_name);
            List<String> brandList = new ArrayList<>();
            if (optionalMerchantConfigs.isPresent()) {
                brandList = optionalMerchantConfigs.get().getValues();
            }
            if (Objects.nonNull(params)) {
                List<String> brandIds = Util.getCombinedBrandIds(params.getBrandId(), params.getBrandIds());
                for (String brandId : brandIds) {
                    Brand brand = this.findById(brandId);
                    if (Objects.nonNull(brand) && Objects.nonNull(brand.getHasProducts()) && brand.getHasProducts()) {
                        if (!CollectionUtils.isEmpty(brandList) && brandList.contains(brandId)) {
                            brands.add(new BrandResponse(brand));
                        }
                    }
                }
            }
        }
        return brands;
    }

    public Brand findById(String brandId) {
        return brandRepository.findById(brandId).orElse(null);
    }

    public Boolean hasBrand(Params merchantParams) {
        if (null != merchantParams) {
            if (null != merchantParams.getBrandIds() && !merchantParams.getBrandIds().isEmpty()) {
                for (String brandId : merchantParams.getBrandIds()) {
                    Brand brand = StringUtils.hasText(brandId) ? this.findById(brandId) : null;
                    if (null != brand && null != brand.getHasProducts() && brand.getHasProducts())
                        return true;
                }
            } else {
                String brandId = merchantParams.getBrandId();
                Brand brand = StringUtils.hasText(brandId) ? this.findById(brandId) : null;
                return null != brand && null != brand.getHasProducts() && brand.getHasProducts();
            }
        }
        return false;
    }

    public String[] filterOnMasterMerchantBrands(String[] brandIds, MerchantUser masterMerchant) {
        if (null == brandIds || brandIds.length == 0 || null == masterMerchant) return brandIds;
        String[] masterMerchantBrands = getMerchantBrandIds(masterMerchant);
        if (null == masterMerchantBrands || masterMerchantBrands.length == 0) return brandIds;
        Set<String> filterBrandSet = new HashSet<>(Arrays.asList(masterMerchantBrands));
        List<String> brands = new ArrayList<>();
        for (String brand : brandIds) {
            if (!filterBrandSet.contains(brand)) brands.add(brand);
        }
        return brands.toArray(new String[0]);
    }

    public List<BrandResponse> getBrand(MerchantUser merchantUser, String brandSearchText, String brandType, MerchantUser masterMerchant) {
        LOGGER.info("Request received to get brand with merchant id : {}, brandType : {}, brandSearchText : {}", merchantUser.getId(), brandType, brandSearchText);
        List<BrandResponse> brands = new ArrayList<>();
        if (!StringUtils.isEmpty(merchantUser)) {
            String[] brandValues = getMerchantBrandIds(merchantUser);
            //to remove master merchant brands, it should only be visible through respective channel like Servify
            String[] brandIds = filterOnMasterMerchantBrands(brandValues, masterMerchant);

            if (brandIds.length > 0) {
                List<Brand> brandList;
                if (StringUtils.hasText(brandType)) {
                    if (BrandType.ALL_BRAND.getType().equalsIgnoreCase(brandType)) {
                        if (StringUtils.hasText(brandSearchText)) {
                            brandList = brandRepository.findByBrandId(brandIds, brandSearchText).orElse(null);
                        } else {
                            brandList = brandRepository.findByBrandId(brandIds).orElse(null);
                        }
                        brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(),
                                merchantUser.getPartner());
                        if (nonPartnerBrandEnabled) {
                            String[] merchantBrandCategories = getMerchantBrandCategories(brandIds);
                            brandList.clear();
                            if (StringUtils.hasText(brandSearchText)) {
                                brandList = brandRepository.findByCategoryAndBrandType(merchantBrandCategories, BrandType.NON_PARTNER.getType(), brandSearchText).orElse(null);
                            } else {
                                brandList = brandRepository.findByCategoryAndBrandType(merchantBrandCategories, BrandType.NON_PARTNER.getType()).orElse(null);
                            }
                            brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(),
                                    merchantUser.getPartner());
                        }
                    } else if (BrandType.NON_PARTNER.getType().equalsIgnoreCase(brandType)) {
                        String[] merchantBrandCategories = getMerchantBrandCategories(brandIds);
                        if (StringUtils.hasText(brandSearchText)) {
                            brandList = brandRepository.findByCategoryAndBrandType(merchantBrandCategories, brandType, brandSearchText).orElse(null);
                        } else {
                            brandList = brandRepository.findByCategoryAndBrandType(merchantBrandCategories, brandType).orElse(null);
                        }
                        brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(),
                                merchantUser.getPartner());
                    }
                } else {
                    if (StringUtils.hasText(brandSearchText)) {
                        brandList = brandRepository.findByBrandId(brandIds, brandSearchText).orElse(null);
                    } else {
                        brandList = brandRepository.findByBrandId(brandIds).orElse(null);
                    }
                    brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(),
                            merchantUser.getPartner());
                }

            }
        }
        return brands;
    }

    public List<BrandResponse> getBrandResponse(List<BrandResponse> brands, List<Brand> brandList, String merchantId,
                                                String partner) {
        if (!CollectionUtils.isEmpty(brandList)) {
            BrandResponse brandResponse;
            for (Brand brand : brandList) {
                if (null != brand.getHasProducts() && brand.getHasProducts()) {
                    brandResponse = new BrandResponse(brand);
                    brandResponse.setIsNoCostEmi(StringUtils.hasText(brand.getDisplayHeader()) && paymentConstants.payment_NO_COST_EMI.equals(brand.getDisplayHeader()));
                    Boolean offerAvailable = null;
                    if (Util.isNotNull(partner)){
                        if (!CollectionUtils.isEmpty(brand.getIsPartnerOfferAvailable())) {
                            offerAvailable = brand.getIsPartnerOfferAvailable().get(partner);
                        }
                    } else {
                        offerAvailable = brand.getIsOfferAvailable();
                    }
                    brandResponse.setIsOfferAvailable(Boolean.TRUE.equals(offerAvailable));
                    brands.add(brandResponse);
                }
            }
        }
        return brands;
    }

    public List<String> findBrandByHasProduct(){
        List<String> brandIds = new ArrayList<>();
        Optional<List<Brand>> brands = brandRepository.findByHasProducts(Boolean.TRUE);
        if(brands.isPresent()) {
            List<Brand> brandWithProduct=brands.get();
            for (Brand brand : brandWithProduct) {
                brandIds.add(String.valueOf(brand.getId()));
            }
        }
        return brandIds;
    }

    public void setIsOfferAvailable(String brandIdRequest) {
        List<String> brandsIds = new ArrayList<>();
        if (!StringUtils.hasText(brandIdRequest)) {
                brandsIds = findBrandByHasProduct();
        }
        else{
            brandsIds.add(brandIdRequest);
        }
        List<Partner> partners = partnerRepository.findAll();
            for (String brandId : brandsIds) {
                Boolean isOfferAvailable = offerBO.isOfferAvailable(brandId, true, null);
                Optional<Brand> brand = brandRepository.findById(brandId);
                if (brand.isPresent()) {
                    if (Boolean.TRUE.equals(isOfferAvailable)) {
                        brand.get().setIsOfferAvailable(Boolean.TRUE);
                    }
                    else{
                        brand.get().setIsOfferAvailable(Boolean.FALSE);
                    }
                    Map<String, Boolean> partnerOfferAvailableMap = new HashMap<>();
                    if (!CollectionUtils.isEmpty(partners)) {
                        for (Partner partner : partners) {
                            if (StringUtils.hasText(partner.getName())) {
                                Boolean isPartnerOfferAvailable = offerBO.isOfferAvailable(brandId, true, partner.getName());
                                if (Boolean.TRUE.equals(isPartnerOfferAvailable)) {
                                    partnerOfferAvailableMap.put(partner.getName(), Boolean.TRUE);
                                }
                            }
                        }
                    }
                        brand.get().setIsPartnerOfferAvailable(partnerOfferAvailableMap);

                    brandRepository.save(brand.get());
                }
            }
    }



    public Brand getBrandById(String brandId) {
        return this.brandRepository.findById(brandId)
                .orElseThrow(() -> new FreewayException("Brand not found"));
    }

    public Brand getBrandByBrandDisplayId(String brandDisplayId) {
        return this.brandRepository.findByBrandDisplayId(brandDisplayId)
                .orElseThrow(() -> new FreewayException("Brand not found"));
    }

    public List<Brand> findByBrandId(String[] brandIds) {
        return brandRepository.findByBrandId(brandIds).orElse(null);
    }

    public List<Brand> findByBrand(String[] brands) {
        return brandRepository.findByBrand(brands).orElse(null);
    }

    public List<Brand> findByCategory(String[] categories) {
        return brandRepository.findByCategory(categories).orElse(null);
    }

    public List<Brand> findByBrandAndCategory(String[] brands, String[] categories) {
        return brandRepository.findByBrandAndCategory(brands, categories).orElse(null);
    }

    public Brand findByIdOrBrandDisplayId(String brandId) {
        return brandRepository.findByIdOrBrandDisplayId(brandId, brandId).orElse(null);
    }

    public String[] getMerchantBrandIds(MerchantUser merchantUser) {
        String[] brandIds = new String[0];
        Params params = merchantUser.getParams();
        List<String> combinedBrandIds = Util.getCombinedBrandIds(params.getBrandId(), params.getBrandIds());
        int counter;
        if (null != combinedBrandIds && !combinedBrandIds.isEmpty()) {
            counter = 0;
            brandIds = new String[combinedBrandIds.size()];
            for (String brandId : combinedBrandIds) {
                brandIds[counter] = brandId;
                counter++;
            }
        }
        return brandIds;
    }

    private String[] getMerchantBrandCategories(String[] merchantBrandIds) {
        String[] categories = new String[0];
        List<Brand> brandList = brandRepository.findByBrandId(merchantBrandIds).orElse(null);
        if (!CollectionUtils.isEmpty(brandList)) {
            categories = new String[merchantBrandIds.length];
            int counter = 0;
            for (Brand brand : brandList) {
                categories[counter] = brand.getCategory();
                counter++;
            }
        }
        return categories;
    }

    public Map<String, String> getBrandMap(MerchantUser merchantUser, String brandSearchText, String brandType) {
        LOGGER.info("Request received to get brand with merchant id : {}, brandType : {}, brandSearchText : {}", merchantUser.getId(), brandType, brandSearchText);
        List<BrandResponse> brands = new ArrayList<>();
        if (!StringUtils.isEmpty(merchantUser)) {
            String[] brandIds = getMerchantBrandIds(merchantUser);
            if (brandIds.length > 0) {
                List<Brand> brandList;
                if (StringUtils.hasText(brandType)) {
                    if (BrandType.ALL_BRAND.getType().equalsIgnoreCase(brandType)) {
                        brandList = brandRepository.findBrandByCategoryByName(brandIds, brandSearchText, brandSearchText, brandSearchText).orElse(null);
                        brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(),
                                merchantUser.getPartner());
                        if (nonPartnerBrandEnabled) {
                            String[] merchantBrandCategories = getMerchantBrandCategories(brandIds);
                            brandList.clear();
                            brandList = brandRepository.findPartnerBrandByCategoryByName(merchantBrandCategories, brandSearchText, brandSearchText, brandSearchText, BrandType.NON_PARTNER.getType()).orElse(null);
                            brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(), merchantUser.getPartner());
                        }
                    } else if (BrandType.NON_PARTNER.getType().equalsIgnoreCase(brandType)) {
                        brandList = brandRepository.findBrandByCategoryByName(brandIds, brandSearchText, brandSearchText, brandSearchText, brandType).orElse(null);
                        brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(), merchantUser.getPartner());
                    }
                } else {
                    brandList = brandRepository.findBrandByCategoryByName(brandIds, brandSearchText, brandSearchText, brandSearchText).orElse(null);
                    brands = getBrandResponse(brands, brandList, merchantUser.getId().toString(), merchantUser.getPartner());
                }
            }
        }
        return getBrandIds(brands);
    }

    private Map<String, String> getBrandIds(List<BrandResponse> brandList) {
        Map<String, String> brandIdMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(brandList)) {
            for (BrandResponse brandResponse : brandList) {
                brandIdMap.put(brandResponse.getBrandId(), brandResponse.getName());
            }
        }
        return brandIdMap;
    }

    public Map<String, String> getMerchantBrandIdMap(MerchantUser merchantUser) {
        Map<String, String> brandIdMap = new HashMap<>();
        Optional<List<Brand>> optionalBrandList = brandRepository.findByBrandId(getMerchantBrandIds(merchantUser));
        if (optionalBrandList.isPresent()) {
            for (Brand brand : optionalBrandList.get()) {
                brandIdMap.put(brand.getId().toString(), brand.getName());
            }
        }
        return brandIdMap;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getBrands() {
        return brandRepository.findByAllBrands();
    }

    public List<Brand> findByProductCategory(String[] productCategories) {
        return brandRepository.findByProductCategory(productCategories).orElse(null);
    }

    public List<Brand> findByBrandIdAndProductCategory(String[] brandIds, String[] productCategories) {
        return brandRepository.findByBrandIdAndProductCategory(brandIds, productCategories).orElse(null);
    }

    public List<ProductCategory> getProductCategory(List<BrandResponse> brands) {
        LOGGER.info("Request received to get all product categories");
        List<ProductCategory> productCategories = null;
        if (!CollectionUtils.isEmpty(brands)) {
            Map<String, ProductCategory> productCategoryMap = new Hashtable<>();
            for (BrandResponse brandResponse : brands) {
                if (!CollectionUtils.isEmpty(brandResponse.getProductCategories())) {
                    for (String category : brandResponse.getProductCategories()) {
                        productCategoryMap.put(category, ProductCategory.builder()
                                .name(category).build());
                    }
                }
            }
            productCategories = new ArrayList<>(productCategoryMap.values());
        }
        return productCategories;
    }

    public Map<String, Brand> getBrandMap() {
        Map<String, Brand> brandIdMap = new HashMap<>();
        List<Brand> optionalBrandList = brandRepository.findAll();
        if (!CollectionUtils.isEmpty(optionalBrandList)) {
            for (Brand brand : optionalBrandList) {
                brandIdMap.put(brand.getId().toString(), brand);
            }
        }
        return brandIdMap;
    }

    public Map<String, Brand> getBrandMap(List<String> brands) {
        Map<String, Brand> brandIdMap = new HashMap<>();
        List<Brand> optionalBrandList = brandRepository.findByBrandId(brands.toArray(new String[0])).orElse(new ArrayList<>());
        if (!CollectionUtils.isEmpty(optionalBrandList)) {
            for (Brand brand : optionalBrandList) {
                brandIdMap.put(brand.getId().toString(), brand);
            }
        }
        return brandIdMap;
    }

    public String[] getBrandId(List<BrandResponse> brandList) {
        String[] brandIds = new String[0];
        if (!CollectionUtils.isEmpty(brandList)) {
            brandIds = new String[brandList.size()];
            int counter = 0;
            for (BrandResponse brand : brandList) {
                brandIds[counter] = brand.getBrandId();
                counter++;
            }
        }
        return brandIds;
    }

    public String[] getAllBrandIds(String brandId) {
        String[] brandIds = new String[0];
        if (StringUtils.hasText(brandId)) {
            brandIds = new String[1];
            brandIds[0] = brandId;
        } else {
            List<Brand> brandList = brandRepository.findAll();
            if (!CollectionUtils.isEmpty(brandList)) {
                brandIds = new String[brandList.size()];
                int counter = 0;
                for (Brand brand : brandList) {
                    brandIds[counter] = brand.getId().toString();
                    counter++;
                }
            }
        }
        return brandIds;
    }

    public String[] getBrandId(Map<String, String> brandMap) {
        String[] brandIds = new String[0];
        if (!CollectionUtils.isEmpty(brandMap)) {
            brandIds = new String[brandMap.size()];
            int counter = 0;
            for (String brand : brandMap.keySet()) {
                brandIds[counter] = brand;
                counter++;
            }
        }
        return brandIds;
    }

    public String[] getBrandIdByBrands(List<Brand> brandList) {
        String[] brandIds = new String[0];
        if (!CollectionUtils.isEmpty(brandList)) {
            brandIds = new String[brandList.size()];
            int counter = 0;
            for (Brand brand : brandList) {
                brandIds[counter] = brand.getId().toString();
                counter++;
            }
        }
        return brandIds;
    }

    public String[] getDataArray(List<String> list) {
        String[] dataArray = new String[0];
        if (!CollectionUtils.isEmpty(list)) {
            dataArray = new String[list.size()];
            int counter = 0;
            for (String brand : list) {
                dataArray[counter] = brand;
                counter++;
            }
        }
        return dataArray;
    }

    public List<BrandResponse> updateBrandOfferDetails(List<String> brandIds) {
        List<Brand> brands = CollectionUtils.isEmpty(brandIds)
                ? this.getAllBrands() : this.findByBrandId(brandIds.toArray(new String[0]));
        if (CollectionUtils.isEmpty(brands)) {
            throw new FreewayException("No brands present for updating offer details");
        }
        return brands.stream().map(this::updateBrandOfferDetails)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public BrandResponse updateBrandOfferDetails(Brand brand) {
        List<Offer> brandOffers = offerBO.findByBrandIdWithSubvention(brand.getId().toString(), null);
        int noCostTenure = -1;
        int lowCostTenure = -1;
        if (!CollectionUtils.isEmpty(brandOffers)) {
            List<String> defaultCreditCardBanks = getCreditCardBanks();
            List<PriceResponse> priceResponses = new ArrayList<>();
            for (Offer offer : brandOffers) {
                if (offer.getTenure() != null || StringUtils.hasText(offer.getType())) {
                    List<BrandProduct> brandProductList = null;
                    if (StringUtils.hasText(offer.getProductId()) && "any".equals(offer.getProductId())) {
                        brandProductList = brandsProductRepository.findByBrandId(offer.getBrandId(), Instant.now(), Boolean.TRUE).orElse(null);
                    } else if (!CollectionUtils.isEmpty(offer.getProductIds())) {
                        brandProductList = brandsProductRepository.findByIds(offer.getProductIds(), Instant.now(), Boolean.TRUE).orElse(null);
                    } else if (StringUtils.hasText(offer.getProductId())) {
                        brandProductList = new ArrayList<>();
                        brandsProductRepository.findById(offer.getProductId()).ifPresent(brandProductList::add);
                    }
                    if (!CollectionUtils.isEmpty(brandProductList)) {
                        for (BrandProduct brandProduct : brandProductList) {
                            priceResponses.addAll(getProductOfferPriceResponses(brand, brandProduct, offer, defaultCreditCardBanks));
                        }
                    } else {
                        LOGGER.info("No products found for brand {} and offer {}", brand.getId().toString(), offer.getId().toString());
                    }
                }
            }
            for (PriceResponse pr : priceResponses) {
                if (isNCE(pr)) {
                    noCostTenure = Math.max(noCostTenure, pr.getTenure());
                } else if (pr.getTenure() != null && pr.getTenure() > 0) {
                    lowCostTenure = Math.max(lowCostTenure, pr.getTenure());
                }
            }
        }
        lowCostTenure = Math.max(noCostTenure, lowCostTenure);

        LOGGER.info("Offer tenure for brand {} - No cost tenure: {}, Low cost tenure: {}", brand.getId().toString(), noCostTenure, lowCostTenure);
        // update offer detail messages accordingly
        if (lowCostTenure > 0) {
            brand.setDisplayHeader(noCostTenure > 0 ? MerchantConstants.BRAND_DISPLAY_HEADER_FORMAT : null);
            brand.setDisplaySubHeader(String.format(MerchantConstants.BRAND_DISPLAY_SUB_HEADER_FORMAT, lowCostTenure));
            brand.setEmiOption(noCostTenure > 0
                    ? String.format(MerchantConstants.BRAND_EMI_OPTION_FORMAT, noCostTenure)
                    : null);
        } else {
            brand.setDisplayHeader(null);
            brand.setDisplaySubHeader(null);
            brand.setEmiOption(null);
        }
        brandRepository.save(brand);
        return new BrandResponse(brand);
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

    private List<PriceResponse> getProductOfferPriceResponses(Brand brand, BrandProduct brandProduct, Offer offer,
                                                              List<String> defaultCreditCardBanks) {
        DecimalFormat df = new DecimalFormat("0.00");
        try {
//            if (StringUtils.hasText(brandProduct.getCategory())) {
            ProductOfferVariant variant = getProductOfferVariant(brandProduct, offer);
            if (null != variant && null != brand) {
                variant.setBrandName(brand.getName());
                variant.setBrandIcon(brand.getIcon());
                ProductOffer productOffer = createProductOffer(offer, df, variant);
                if (offer.getTenure() == -1) {
                    productOffer.setTenures(offerDefaultTenures);
                } else {
                    List<Integer> tenures = new ArrayList<>();
                    tenures.add(offer.getTenure());
                    productOffer.setTenures(tenures);
                }
                if (StringUtils.hasText(offer.getBankCode())) {
                    List<String> banks = new ArrayList<>();
                    banks.add(offer.getBankCode());
                    if (CardTypeEnum.CREDIT.name().equals(offer.getCardType())) {
                        productOffer.setCreditCard(banks);
                    } else {
                        productOffer.setPreApprovedCard(banks);
                    }
                } else {
                    if (CardTypeEnum.CREDIT.name().equals(offer.getCardType())) {
                        productOffer.setCreditCard(defaultCreditCardBanks);
                    } else {
                        productOffer.setPreApprovedCard(defaultPreApprovedBanks);
                    }
                }
                if (null != productOffer.getVariant().getAmount() && productOffer.getVariant().getAmount() > 0 &&
                        null != productOffer.getSubvention() && productOffer.getSubvention() > 0) {
                    return calculatePriceResponses(productOffer, offer);
                } else {
                    LOGGER.info("No subvention offer {} for brand {}", offer.getId().toString(), offer.getBrandId());
                }
            }
//            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return new ArrayList<>();
    }

    private static ProductOffer createProductOffer(Offer offer, DecimalFormat df, ProductOfferVariant variant) {
        ProductOffer productOffer = new ProductOffer();
        productOffer.setOfferId(offer.getId().toString());
        productOffer.setVariant(variant);
        productOffer.setType(offer.getType());
        productOffer.setOfferDescription(offer.getOfferDescription());
        productOffer.setIsValid(true);
        productOffer.setValidFrom(offer.getValidFrom());
        productOffer.setValidTo(offer.getValidTo());
        productOffer.setMaxOfferAmount(null != offer.getMaxOfferAmount() ? offer.getMaxOfferAmount() : 0);
        productOffer.setOfferPercentage(null != offer.getOfferPercentage() ? Float.parseFloat(df.format(offer.getOfferPercentage())) : 0);
        productOffer.setPreApprovedCard(new ArrayList<>());
        productOffer.setCreditCard(new ArrayList<>());
        productOffer.setSubvention(Float.parseFloat(df.format(offer.getSubvention())));
        return productOffer;
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

    private List<PriceResponse> calculatePriceResponses(ProductOffer productOffers, Offer offer) {
        List<PriceResponse> priceResponseList = new ArrayList<>();
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
                    InterestPerTenureDto interestPerTenureDto = schemeConfigBO.getStandardInterestPerTenure(offer.getCardType(), bankCode, tenure, providerMasterConfigInfoMap);
                    float cashback = Float.parseFloat(df.format(productOffers.getVariant().getAmount() * cashbackRate));
                    Float irrpa = null;
                    if (Objects.nonNull(interestPerTenureDto) && Objects.nonNull(interestPerTenureDto.getIrr())) {
                        irrpa = interestPerTenureDto.getIrr().floatValue();
                    }
                    if (irrpa != null) {
                        pr = paymentOptionsBO.getPgPriceResponse(offer.getCardType(), bankCode,
                                tenure, discount, productOffers.getVariant().getAmount(),
                                splitAmount, cFee,
                                cashback, null, false, processingFeeRate, maxProcessingFee, ccieName, irrpa, 0, false,
                                null);
                        if (pr != null) {
                            priceResponseList.add(pr);
                        }
                    } else {
                        LOGGER.info("Bank interest not available for {} {} {}", offer.getCardType(), bankCode, tenure);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }
        return priceResponseList;
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

    private boolean isNCE(PriceResponse pr) {
        Float cashbackDiscount = pr.getCashback() > 0 ? pr.getCashback() : pr.getDiscount();
        return pr.getBankCharges() - cashbackDiscount == 0;
    }

    // We can modify brand search as and when we get new search criteria.
    public List<BrandResponse> searchBrands(Boolean scheduledUnclaim) {
        List<BrandResponse> responseList = new ArrayList<>();
        if (scheduledUnclaim == null) {
            return responseList;
        }
        List<Brand> brands = brandRepository.findByScheduledUnclaim(scheduledUnclaim).orElse(new ArrayList<>());
        for (Brand brand: brands) {
            BrandResponse brandResponse = new BrandResponse(String.valueOf(brand.getId()));
            brandResponse.setName(brand.getName());
            responseList.add(brandResponse);
        }
        return responseList;
    }

    public List<BrandInfoRepsonse> brandsInfo(Integer offset, Integer limit){
        Stream<Brand> brands;
        if(offset!=null && limit!=null) {
            brands = brandRepository.findAll(Sort.by(Sort.Order.by("name"))).stream().skip(offset).limit(limit);
        }
        else {
            brands = brandRepository.findAll(Sort.by(Sort.Order.by("name"))).stream();
        }
        List<BrandInfoRepsonse> brandsRepsonse = new ArrayList<>();
            if (Util.isNotNull(brands)) {
             brands.forEach(i -> brandsRepsonse.add(new BrandInfoRepsonse(i.getId().toString(), i.getName())));

        }
        return brandsRepsonse;
    }

    public Brand getByBrandCodeOrName(String brandCodeOrName) {
        return brandRepository.findByBrandCode(brandCodeOrName)
                .orElseGet(() -> brandRepository.findByName(brandCodeOrName).orElse(null));
    }

    @Cacheable(value = "cacheManagerForAnyExpiry", key = "#brandId")
    public String getBrandName(String brandId){
        Brand brand = brandRepository.findById(brandId).orElse(null);
        if(Util.isNotNull(brand)){
            return brand.getName();
        }
        else{
            throw new MerchantException(MerchantResponseCode.INVALID_BRAND);
        }
    }

    public List<Brand> getAllBrands(Pageable pageable){
        List<Brand> brands;
        if(Util.isNotNull(pageable)) {
            brands = brandRepository.findAll(pageable).getContent();
        }
        else{
            brands = brandRepository.findAll();
        }
        return brands;
    }
}
