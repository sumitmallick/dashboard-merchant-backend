package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.ProductInfos;
import com.freewayemi.merchant.dto.ProductNameIdInfo;
import com.freewayemi.merchant.dto.request.BrandProductRequestDto;
import com.freewayemi.merchant.dto.request.GlobalSearchRequestDto;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.BrandProduct;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.BrandType;
import com.freewayemi.merchant.repository.BrandsProductRepository;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

@Component
public class BrandProductBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandProductBO.class);

    private final BrandsProductRepository brandsProductRepository;
    private final BrandBO brandBO;
    private final MerchantUserBO merchantUserBO;
    private final OfferBO offerBO;

    @Autowired
    public BrandProductBO(BrandsProductRepository brandsProductRepository,
                          BrandBO brandBO, MerchantUserBO merchantUserBO,
                          OfferBO offerBO) {
        this.brandsProductRepository = brandsProductRepository;
        this.brandBO = brandBO;
        this.merchantUserBO = merchantUserBO;
        this.offerBO = offerBO;
    }

    public List<BrandsProductResponse> get(String brandId) {
        List<BrandProduct> brandProducts = brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE).orElse(null);
        Map<String, List<Variant>> productVariants = new LinkedHashMap<>();
        brandProducts.forEach(brandProduct -> {
            if (productVariants.containsKey(brandProduct.getProduct())) {
                productVariants.get(brandProduct.getProduct()).add(new Variant(brandProduct));
            } else {
                productVariants.put(brandProduct.getProduct(),
                        StringUtils.hasText(brandProduct.getVariant()) ?
                                new ArrayList<Variant>() {{
                                    add(new Variant(brandProduct));
                                }} : new ArrayList<>());
            }
        });
        List<BrandsProductResponse> brandsProductResponseList = new ArrayList<>();
        productVariants.forEach((productName, variants) -> {
            brandsProductResponseList.add(new BrandsProductResponse(productName, variants));
        });
        return brandsProductResponseList;
    }

    public List<BrandsProductResponse> get(String brandId, String barcodeText) {
        if (!StringUtils.hasText(barcodeText)) {
            return get(brandId);
        }
        // Bridgestone requirement -> match first 3 digits of barcode with model number.
        LOGGER.info("Request to get products for brandId: {} with barcode text: {}", brandId, barcodeText);
        String modelNo = barcodeText.length() > 3 ? barcodeText.substring(0, 3) : barcodeText;
        List<BrandProduct> brandProducts = brandsProductRepository.findByBrandIdAndModelNo(brandId, modelNo,
                Instant.now(), Boolean.TRUE).orElse(null);
        Map<String, List<Variant>> productVariants = new LinkedHashMap<>();
        brandProducts.forEach(brandProduct -> {
            if (productVariants.containsKey(brandProduct.getProduct())) {
                productVariants.get(brandProduct.getProduct()).add(new Variant(brandProduct));
            } else {
                List<Variant> variants = new ArrayList<>();
                if (StringUtils.hasText(brandProduct.getVariant())) {
                    variants.add(new Variant(brandProduct));
                }
                productVariants.put(brandProduct.getProduct(), variants);
            }
        });
        List<BrandsProductResponse> brandsProductResponseList = new ArrayList<>();
        productVariants.forEach((productName, variants) -> {
            brandsProductResponseList.add(new BrandsProductResponse(productName, variants));
        });
        return brandsProductResponseList;
    }

    public List<BrandProductsResponse> getV2(String brandId) {
        List<BrandProduct> brandProducts = brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE).orElse(null);
        List<BrandProductsResponse> brandProductsResponseList = new ArrayList<>();
        brandProducts.forEach(brandProduct -> {
            brandProductsResponseList.add(new BrandProductsResponse(brandProduct, null));
        });
        return brandProductsResponseList;
    }

    public void save(BrandProduct brandProduct) {
        brandsProductRepository.save(brandProduct);
    }

    public BrandInfo getBrandInfo(BrandRequest brandRequest) {
        LOGGER.info("get brand info {}", brandRequest);
//        BrandProduct brandProduct = null != brandRequest && StringUtils.hasText(brandRequest.getBrandProductId()) ? getById(brandRequest.getBrandProductId()) :
//                null != brandRequest && StringUtils.hasText(brandRequest.getModelNumber()) && StringUtils.hasText(brandRequest.getBrandId()) ?
//                        getByModelNumberAndBrandId(brandRequest.getModelNumber(), brandRequest.getBrandId()) : null;
        BrandProduct brandProduct = null;
        Brand brand = null;
        if (null != brandRequest && StringUtils.hasText(brandRequest.getBrandProductId())) {
            brandProduct = getById(brandRequest.getBrandProductId());
        }
        if (null == brandProduct && null != brandRequest && StringUtils.hasText(brandRequest.getProductSkuCode())) {
            brandProduct = getBySkuCodeOrModelNo(brandRequest.getProductSkuCode());
        }
        if (null == brandProduct && null != brandRequest && StringUtils.hasText(brandRequest.getModelNumber()) && StringUtils.hasText(brandRequest.getBrandId())) {
            brand = brandBO.findByIdOrBrandDisplayId(brandRequest.getBrandId());
            brandProduct = null != brand ? getByModelNumberAndBrandId(brandRequest.getModelNumber(), brand.getId().toString()) : null;
        }
        if (null != brandProduct) {
            brand = brandBO.findById(brandProduct.getBrandId());
            MarginMoneyConfigDto marginMoneyConfigDto = new MarginMoneyConfigDto(brand.getMarginMoneyConfig());
            marginMoneyConfigDto.setMaxMarginDpAmount(brandProduct.getMaxMarginDpAmount());
            marginMoneyConfigDto.setMinMarginDpAmount(brandProduct.getMinMarginDpAmount());
            return new BrandInfo(
                    brand.getId().toString(),
                    brandRequest.getSerialNumber(),
                    brandProduct.getId().toString(),
                    brand.getIcon(),
                    brand.getSideBanner(),
                    brandProduct.getEmiOption(),
                    brandProduct.getProduct(),
                    brandProduct.getVariant(),
                    brandProduct.getModelNo(),
                    brand.getName(),
                    null,
                    brand.getPaymentCycle(), brandProduct.getAmount(), brandProduct.getMinAmount(),
                    brand.getFraudChecks(),
                    brand.getPurchaseVelocity(),
                    brand.getIsBrandMdrModel(),
                    brand.getSubventionType(),
                    null,
                    brandProduct.getNBFCSchemeID(),
                    brand.getBrandDisplayId(),
                    brand.getEmiOfferType(),
                    brand.getBrandParams(),
                    StringUtils.hasText(brand.getCategory()) ? brand.getCategory() : brandProduct.getCategory(),
                    brand.getIsBrandAdditionalCashbackValidationModel(), brand.getBrandFeeRateInstantDiscount(),
                    marginMoneyConfigDto,
                    brand.getBrandProductSku(),
                    brand.getBrandAPI(),
                    brand.getAsyncReport(),
                    brand.getAsyncUnclaim(),
                    brand.getBarcodeScanEnabled(),
                    brand.getHideProductAmount(),
                    brand.getFreezePaymentModeOnSerialNumber(),
                    brandProduct.getCategory(),
                    VelocityConfigDto.fromVelocityConfig(brand.getVelocityConfig()));
        }
        return null;
    }

    private BrandProduct getByModelNumberAndBrandId(String modelNumber, String brandId) {
        return brandsProductRepository.findByModelNoAndBrandId(modelNumber, brandId, Instant.now(), Boolean.TRUE).orElse(null);
    }

    public BrandProduct getById(String brandProductId) {
        return brandsProductRepository.findById(brandProductId).orElse(null);
    }

    public List<BrandCityStoreDTO> getBrandStore(String brandId, String city, Integer limit, Integer offset) {
        List<BrandCityStoreDTO> brandStoreList = new ArrayList<BrandCityStoreDTO>();
        BrandCityStoreDTO brandCityStoreDTO;
        List<MerchantUser> totalStoreList = merchantUserBO.getBrandStore(brandId, city);
        if (!CollectionUtils.isEmpty(totalStoreList)) {
            for (MerchantUser merchantUser : merchantUserBO.getBrandStore(brandId, city, limit, offset)) {
                brandCityStoreDTO = new BrandCityStoreDTO();
                brandCityStoreDTO.setAddress(merchantUser.getAddress());
                brandCityStoreDTO.setShopName(merchantUser.getShopName());
                brandCityStoreDTO.setMobile(merchantUser.getMobile());
                brandCityStoreDTO.setTotalCount(totalStoreList.size());
                brandStoreList.add(brandCityStoreDTO);
            }
        }
        return brandStoreList;
    }

    public String checkBrandProductStatus(MerchantUser merchantUser) {
        try {
            if (null != merchantUser && null != merchantUser.getParams()) {
                Params params = merchantUser.getParams();
                List<BrandProduct> brandProducts = null;
                if (null != params.getBrandIds() && !params.getBrandIds().isEmpty()) {
                    for (String brandId : params.getBrandIds()) {
                        brandProducts = brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE).orElse(null);
                        if (!CollectionUtils.isEmpty(brandProducts))
                            return "true";
                    }
                } else if (!StringUtils.isEmpty(params.getBrandId())) {
                    brandProducts = brandsProductRepository.findByBrandId(params.getBrandId(), Instant.now(), Boolean.TRUE).orElse(null);
                    if (!CollectionUtils.isEmpty(brandProducts))
                        return "true";
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while checking brand products : {}", e.getMessage());
        }
        return "false";
    }

    public List<BrandsProductResponse> getV2Products(String brandId, String merchantId,
                                                     BrandProductRequestDto brandProductRequestDto) {
        LOGGER.info("Received request to get products with brand id: {}, params :{}", brandId, brandProductRequestDto);
        List<BrandsProductResponse> brandsProductResponseList = new ArrayList<>();
        List<BrandProduct> brandProductList = null;
        // Bridgestone requirement -> match first 3 digits of barcode with model number.
        String barcodeText = brandProductRequestDto.getBarcodeText() != null && brandProductRequestDto.getBarcodeText().length() > 3 ?
                brandProductRequestDto.getBarcodeText().substring(0, 3) : brandProductRequestDto.getBarcodeText();
        Pageable pageable = new OffsetBasedPageRequest(brandProductRequestDto.getLimit(), brandProductRequestDto.getOffset(), new Sort(Sort.Direction.ASC, "id"));
        if (null != brandProductRequestDto.getIsPopular() && brandProductRequestDto.getIsPopular()) {
            if (StringUtils.hasText(barcodeText)) {
                brandProductList = brandsProductRepository.findByBrandIdAndModelNo(brandId, barcodeText, Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (null != brandProductRequestDto.getCategory() && brandProductRequestDto.getCategory().length > 0 && StringUtils.hasText(brandProductRequestDto.getSearchProduct())) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getCategory(), Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (null != brandProductRequestDto.getCategory() && brandProductRequestDto.getCategory().length > 0) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getCategory(), Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (StringUtils.hasText(brandProductRequestDto.getSearchProduct())) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else {
                brandProductList = brandsProductRepository.findByBrandId(brandId, Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            }
        } else {
            if (StringUtils.hasText(barcodeText)) {
                brandProductList = brandsProductRepository.findByBrandIdAndModelNo(brandId, barcodeText, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (null != brandProductRequestDto.getCategory() && brandProductRequestDto.getCategory().length > 0 && StringUtils.hasText(brandProductRequestDto.getSearchProduct())) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getCategory(), Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (null != brandProductRequestDto.getCategory() && brandProductRequestDto.getCategory().length > 0) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getCategory(), Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else if (StringUtils.hasText(brandProductRequestDto.getSearchProduct())) {
                brandProductList = brandsProductRepository.findByBrandId(brandId, brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), brandProductRequestDto.getSearchProduct(), Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else {
                brandProductList = brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            }
        }
        Map<String, List<Variant>> productVariantMap = new LinkedHashMap<>();
        brandProductList.forEach(brandProduct -> {
            if (productVariantMap.containsKey(brandProduct.getProduct())) {
                productVariantMap.get(brandProduct.getProduct()).add(new Variant(brandProduct));
            } else {
                productVariantMap.put(brandProduct.getProduct(),
                        StringUtils.hasText(brandProduct.getVariant()) ?
                                new ArrayList<Variant>() {{
                                    brandProduct.setDisplayHeader("");
                                    add(new Variant(brandProduct));
                                }} : new ArrayList<>());
            }
        });
        productVariantMap.forEach((productName, variants) -> {
            brandsProductResponseList.add(new BrandsProductResponse(productName, variants));
        });
        return productHeaderCheck(brandsProductResponseList, brandId, brandProductRequestDto.getCardType(), merchantId);
    }

    private List<BrandsProductResponse> productHeaderCheck(List<BrandsProductResponse> brandsProductResponseList,
                                                           String brandId, String cardType, String merchantId) {
        LOGGER.info("Received request to product Header Check for brand id: {}, cardType : {}", brandId, cardType);
        Brand brand = brandBO.findById(brandId);
        if (BrandType.NON_PARTNER.getType().equals(brand.getBrandType())) {
            return brandsProductResponseList;
        } else {
            MerchantUser mu = merchantUserBO.getUserById(merchantId);
            List<OfferResponse> offerResponseList = offerBO.getBrandSubventions(brandId, cardType, merchantId, mu.getPartner());
            if (CollectionUtils.isEmpty(offerResponseList)) {
                return brandsProductResponseList;
            } else {
                List<BrandsProductResponse> brandsProductResponseFilterList = new ArrayList<>();
                List<Variant> variants;
                Map<String, String> productMap = new LinkedHashMap<>();
                for (OfferResponse offerResponse : offerResponseList){
                    if ("any".equalsIgnoreCase(offerResponse.getType())) {
                        for (BrandsProductResponse brandsProductResponse : brandsProductResponseList) {
                            variants = new ArrayList<>();
                            for (Variant variant : brandsProductResponse.getVariants()) {
                                variant.setDisplayHeader("No-Cost EMI");
                                variants.add(variant);
                            }
                            brandsProductResponseFilterList.add(new BrandsProductResponse(brandsProductResponse.getName(), variants));
                        }
                        return brandsProductResponseList;
                    }else {
                        if (null != offerResponse.getProductIds() && offerResponse.getProductIds().size() > 0){
                            offerResponse.getProductIds().forEach(brandProductId -> {
                                productMap.put(brandProductId, brandProductId);
                            });
                    }
                    }
                }
                for (BrandsProductResponse brandsProductResponse : brandsProductResponseList) {
                    variants = new ArrayList<>();
                    for (Variant variant : brandsProductResponse.getVariants()) {
                        if (productMap.containsKey(variant.getBrandProductId())) {
                            variant.setDisplayHeader("No-Cost EMI");
                        }
                        variants.add(variant);
                    }
                    brandsProductResponseFilterList.add(new BrandsProductResponse(brandsProductResponse.getName(), variants));
                }
            }
        }
        return brandsProductResponseList;
    }

    public Map<String, String> filterOnMasterMerchantBrands(Map<String, String> brandIdMap, MerchantUser masterMerchant) {
        if (null == brandIdMap || brandIdMap.isEmpty() || null == masterMerchant) return brandIdMap;
        Map<String, String> masterMerchantBrandIdMap = brandBO.getMerchantBrandIdMap(masterMerchant);
        if (null == masterMerchantBrandIdMap || masterMerchantBrandIdMap.isEmpty()) return brandIdMap;
        Map<String, String> brandMap = new HashMap<>();
        Set<String> filterSet = masterMerchantBrandIdMap.keySet();
        brandIdMap.forEach((k, v) -> {
            if (!filterSet.contains(k)) brandMap.put(k, v);
        });
        return brandMap;
    }


    public List<BrandsProductResponse> getBrandsProducts(String merchantId, GlobalSearchRequestDto globalSearchRequestDto) {
        LOGGER.info("Received request to get products with merchant id: {}, params :{}", merchantId, globalSearchRequestDto);
        MerchantUser merchantUser = merchantUserBO.getUserByIdOrDisplayId(merchantId);
        Map<String, String> brandIdMapValues = brandBO.getMerchantBrandIdMap(merchantUser);
        //to remove brand map values of the master merchant.
        MerchantUser masterUser = null;
        if (StringUtils.hasText(merchantUser.getMasterMerchants())) {
            masterUser = merchantUserBO.getUserByIdOrDisplayId(merchantUser.getMasterMerchants());
        }
        Map<String, String> brandIdMap = filterOnMasterMerchantBrands(brandIdMapValues, masterUser);

        Map<String, String> filteredBrandIdMap = brandBO.getBrandMap(merchantUser, globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getBrandType());

        List<BrandsProductResponse> brandsProductResponseList = new ArrayList<>();
        if (null != brandIdMap && brandIdMap.size() > 0) {
            List<BrandProduct> brandProductList = null;
            Pageable pageable = new OffsetBasedPageRequest(globalSearchRequestDto.getLimit(), globalSearchRequestDto.getOffset(), new Sort(Sort.Direction.ASC, "id"));
            if (null != globalSearchRequestDto.getIsPopular() && globalSearchRequestDto.getIsPopular()) {
                brandProductList = brandsProductRepository.findByBrandId(getMerchantBrandIds(brandIdMap), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), getMerchantBrandIds(filteredBrandIdMap), Boolean.TRUE, Instant.now(), Boolean.TRUE, pageable).orElse(null);
            } else {
                brandProductList = brandsProductRepository.findByBrandId(getMerchantBrandIds(brandIdMap), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), globalSearchRequestDto.getSearchText(), getMerchantBrandIds(filteredBrandIdMap), Instant.now(), Boolean.TRUE, pageable).orElse(null);
            }
            Map<String, List<Variant>> productVariantMap = new LinkedHashMap<>();
            if (!CollectionUtils.isEmpty(brandProductList)) {
                for (BrandProduct brandProduct : brandProductList) {
                    if (!productVariantMap.containsKey(brandProduct.getProduct())) {
                        productVariantMap.put(brandProduct.getProduct(), new ArrayList<>());
                    }
                    Variant variant = new Variant(brandProduct);
                    variant.setBrandName(brandIdMap.get(variant.getBrandId()));
                    productVariantMap.get(brandProduct.getProduct()).add(variant);
                }
            }
            productVariantMap.forEach((productName, variants) -> {
                brandsProductResponseList.add(new BrandsProductResponse(productName, variants));
            });
        } else {
            LOGGER.info("Merchant's brand list is empty");
        }
        return brandsProductResponseList;
    }

    private String[] getMerchantBrandIds(Map<String, String> brandIdMap) {
        String[] brandIds = new String[brandIdMap.size()];
        int counter = 0;
        for (String brandId : brandIdMap.keySet()) {
            brandIds[counter] = brandId;
            counter++;
        }
        return brandIds;
    }

    public List<BrandProduct> findByIds(List<String> brandProductIds) {
        return brandsProductRepository.findByIds(brandProductIds, Instant.now(), Boolean.TRUE).orElse(null);
    }

    public List<BrandProduct> findByBrandId(String brandId) {
        return brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE).orElse(null);
    }

    public BrandProductsResponse getProductDetails(String productId) {
        try {
            BrandProduct brandProduct = brandsProductRepository.findByBrandProductId(productId, Instant.now(), Boolean.TRUE).orElse(null);
            String brandName = null;
            if (Util.isNotNull(brandProduct)) {
                String brandId = brandProduct.getBrandId();
                if (Util.isNotNull(brandId)) {
                    List<Brand> brands = brandBO.findByBrandId(new String[]{brandId});
                    if (Util.isNotNull(brands) && brands.size() > 0) {
                            brandName = brands.get(0).getName();
                    }
                }
            }
            return new BrandProductsResponse(brandProduct, brandName);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching product with id: " + productId, e);
            throw new FreewayException(
                    "Exception occurred while fetching product with id: " + productId);
        }
    }

    public BrandProduct getBySkuCode(String skuCode) {
        if (StringUtils.isEmpty(skuCode)) {
            return null;
        }
        return brandsProductRepository.findBySkuCode(skuCode, Instant.now(), Boolean.TRUE).orElse(null);
    }

    public BrandProduct getBySkuCodeOrModelNo(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return brandsProductRepository.findBySkuCode(code, Instant.now(), true)
                .orElseGet(() -> brandsProductRepository.findByModelNo(code, Instant.now(), true).orElse(null));
    }


    @Cacheable(value = "cacheManagerForProducts", cacheManager = "cacheManagerForProducts", key ="'PRODUCT_INFO_' + #brandId")
    public List<ProductInfos> getProductsInfo(String brandId){
        LOGGER.info("brandId: {}", brandId);
        List<BrandProduct> brandProducts = brandsProductRepository.findByBrandId(brandId, Instant.now(), Boolean.TRUE).orElse(null);
        List<ProductInfos> productInfos = new ArrayList<>();
        HashMap<String,List<ProductNameIdInfo>> categoryProductMap = new HashMap<>();
        if(Util.isNotNull(brandProducts) && !CollectionUtils.isEmpty(brandProducts)) {

            for (BrandProduct brandProduct : brandProducts) {
                if(Util.isNotNull(brandProduct) && StringUtils.hasText(brandProduct.getCategory()) && StringUtils.hasText(brandProduct.getProduct())) {
                    ProductNameIdInfo product ;
                    String brandProductVariantModel = brandProduct.getProduct() + "," + brandProduct.getVariant() + "," + brandProduct.getModelNo();
                    product= ProductNameIdInfo.builder().productName(brandProductVariantModel).productId(brandProduct.getId().toString()).build();
                    categoryProductMap.computeIfAbsent(brandProduct.getCategory(), k -> new ArrayList<>());
                    categoryProductMap.get(brandProduct.getCategory()).add(product);
                }
            }

            for(String key: categoryProductMap.keySet()){
                ProductInfos productInfos1 = ProductInfos.builder().category(key).productNameIdInfoList(categoryProductMap.get(key)).build();
                productInfos.add(productInfos1);
            }
            LOGGER.info("ProductInfos: {}", productInfos);
            return productInfos;
        }
        else{
           throw new MerchantException(MerchantResponseCode.INVALID_PRODUCT);
        }
    }

    public BrandProduct getBrandProduct(String productId) {
        return brandsProductRepository.findByBrandProductId(productId, Instant.now(), Boolean.TRUE).orElse(null);
    }
}
