package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.dto.BrandInfo;
import com.freewayemi.merchant.commons.dto.BrandRequest;
import com.freewayemi.merchant.bo.BrandBO;
import com.freewayemi.merchant.bo.BrandProductBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.dto.BrandInfoRepsonse;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.commons.utils.Util;

import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.request.BrandProductRequestDto;
import com.freewayemi.merchant.dto.request.GlobalSearchRequestDto;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.MerchantAuthSource;


import com.freewayemi.merchant.service.MerchantAuthService;

import com.freewayemi.merchant.enums.OpsDashboardResponseCode;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.service.MerchantAuthService;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;

import com.freewayemi.merchant.service.MerchantAuthService;
import com.freewayemi.merchant.service.OpsDashboardService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class BrandController {
    private final Logger LOGGER = LoggerFactory.getLogger(BrandController.class);
    private final BrandProductBO brandProductBO;
    private final BrandBO brandBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantAuthService merchantAuthService;
    private final OpsDashboardService opsDashboardService;

    @Autowired
    public BrandController(BrandProductBO brandProductBO, BrandBO brandBO,
                           MerchantUserBO merchantUserBO, MerchantAuthService merchantAuthService, OpsDashboardService opsDashboardService) {
        this.brandProductBO = brandProductBO;
        this.brandBO = brandBO;
        this.merchantUserBO = merchantUserBO;
        this.merchantAuthService = merchantAuthService;
        this.opsDashboardService = opsDashboardService;
    }

    @GetMapping("/api/v1/{mid}/brands")
    public List<BrandResponse> get(@PathVariable("mid") String merchantId) {
        MerchantUser user = merchantUserBO.getUserById(merchantId);
        return brandBO.get(user, true);
    }

    @GetMapping("/api/v1/{mid}/brands/{bid}/products")
    public List<BrandsProductResponse> getProducts(@PathVariable("mid") String merchantId,
                                                   @PathVariable("bid") String brandId,
                                                   @RequestParam(value = "barcodeText", required = false) String barcodeText) {
        return brandProductBO.get(brandId, barcodeText);
    }

    @GetMapping("/api/v2/brand/{bid}/products")
    public List<BrandProductsResponse> getProductsV2(@PathVariable("bid") String brandId) {
        return brandProductBO.getV2(brandId);
    }

    @GetMapping("/api/v1/brands/{bid}/stores")
    public List<BrandCityStoreDTO> getBrandStore(
            @PathVariable("bid") String brandId,
            @RequestParam("city") String city,
            @RequestParam("limit") Integer limit,
            @RequestParam("offset") Integer offset
    ) {
        return brandProductBO.getBrandStore(brandId, city, limit, offset);
    }

    @GetMapping("/api/v3/{mid}/brands")
    public List<BrandResponse> getBrandV3(@PathVariable("mid") String merchantId,
                                          @RequestParam(value = "brand", required = false
                                          ) String brandSearchText,
                                          @RequestParam(value = "brandType", required = false
                                          ) String brandType
    ) {
        MerchantUser user = merchantUserBO.getUserById(merchantId);
        MerchantUser masterUser = StringUtils.hasText(user.getMasterMerchants()) ? merchantUserBO.getUserByIdOrDisplayId(user.getMasterMerchants()) : null;
        return brandBO.getBrand(user, brandSearchText, brandType, masterUser);
    }
    @PostMapping("/api/v1/brands/update/offerAvailable")
    public void setOfferAvailable(@RequestParam(value = "brandId", required = false) String brandId){
        brandBO.setIsOfferAvailable(brandId);
    }

    @PostMapping("/api/v2/{mid}/brands/{bid}/products")
    public List<BrandsProductResponse> getV2Products(@PathVariable("mid") String merchantId,
                                                     @PathVariable("bid") String brandId,
                                                     @Valid @RequestBody BrandProductRequestDto brandProductRequestDto) {
        return brandProductBO.getV2Products(brandId, merchantId, brandProductRequestDto);
    }

    @PostMapping("/api/v1/{mid}/brandsProducts")
    public List<BrandsProductResponse> getBrandsProducts(@PathVariable("mid") String merchantId,
                                                         @Valid @RequestBody GlobalSearchRequestDto globalSearchRequestDto) {
        return brandProductBO.getBrandsProducts(merchantId, globalSearchRequestDto);
    }

    @PostMapping("/api/v2/brandInfo")
    public BrandInfo getBrandInfo(@RequestBody BrandRequest brandRequest) {
        LOGGER.info("Received brand info request brandRequest: {}", brandRequest);
        return brandProductBO.getBrandInfo(brandRequest);
    }

    @PostMapping("/api/v1/brand/offerDetails/update")
    public List<BrandResponse> updateBrandOffer(@RequestParam(value = "brandIds", required = false) List<String> brandIds) {
        LOGGER.info("Request received to update offer details for brands {}", brandIds);
        return brandBO.updateBrandOfferDetails(brandIds);
    }

    @GetMapping("/api/v1/products/{pid}")
    public BrandProductsResponse getProductDetails(@PathVariable("pid") String productId) {
        return brandProductBO.getProductDetails(productId);
    }

    @GetMapping("/api/v1/brands/search")
    public List<BrandResponse> searchBrands(@RequestParam(value = "scheduledUnclaim") Boolean scheduledUnclaim) {
        return brandBO.searchBrands(scheduledUnclaim);
    }

    @GetMapping("/internal/api/v1/brands/info")
    public BrandMerchantResponse getBrands(@RequestParam(value = "brandIds",required = false) List<String> brandIds, @RequestParam(value = "skip",required = false)Integer skip, @RequestParam(value = "limit",required = false)Integer limit, HttpServletRequest request){
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).source(MerchantAuthSource.INTERNAL).build();
        merchantAuthService.authenticate(merchantAuthDto);
        return opsDashboardService.getBrandInfo(brandIds,skip,limit);
    }
}
