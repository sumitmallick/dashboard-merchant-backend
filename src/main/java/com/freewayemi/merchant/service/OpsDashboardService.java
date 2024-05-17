package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.BrandBO;
import com.freewayemi.merchant.bo.BrandProductBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.ProductInfos;
import com.freewayemi.merchant.dto.response.BrandMerchantResponse;
import com.freewayemi.merchant.dto.response.ProductInfoResponse;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.enums.OpsDashboardResponseCode;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class OpsDashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsDashboardService.class);
    private final BrandBO brandBO;
    private final BrandProductBO brandProductBO;
    private final MerchantUserBO merchantUserBO;
    public OpsDashboardService(BrandBO brandBO, BrandProductBO brandProductBO, MerchantUserBO merchantUserBO){
        this.brandBO=brandBO;
        this.brandProductBO=brandProductBO;
        this.merchantUserBO = merchantUserBO;
    }

    public BrandMerchantResponse getBrandInfo(List<String> brandIds,Integer skip, Integer limit) {
        BrandMerchantResponse brandMerchantResponse;
        HashMap<String, String> brandsInfo = new HashMap<>();
        List<Brand> brands = new ArrayList<>();
        if (Util.isNotNull(brandIds) && !CollectionUtils.isEmpty(brandIds)) {
            List<String> invalidBrandIds = new ArrayList<>();
            for (String brandId : brandIds) {
                try {
                    String brandName = brandBO.getBrandName(brandId);
                    if (StringUtils.hasText(brandName)) {
                        brandsInfo.put(brandId, brandName);
                    }
                } catch (Exception e) {
                    invalidBrandIds.add(brandId);
                }
            }
            if (invalidBrandIds.size() != 0) {
                String statusMessage = invalidBrandIds.toString() + "BrandIds are Invalid";
                brandMerchantResponse = BrandMerchantResponse.builder().brandMerchantResp(brandsInfo).message(statusMessage).code(OpsDashboardResponseCode.INVALID_BRAND.getCode()).status(Status.FAILED.getStatus()).build();
            } else {
                brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.SUCCESS.getCode()).brandMerchantResp(brandsInfo).message(OpsDashboardResponseCode.SUCCESS.getMessage()).status(Status.SUCCESS.getStatus()).build();
            }
            return brandMerchantResponse;

        } else {
            Pageable pageable = null;
            if (skip != null && limit != null) {
                pageable = new OffsetBasedPageRequest(limit, skip, new Sort(Sort.Direction.ASC, "name"));
            }
            brands = brandBO.getAllBrands(pageable);

            if (Util.isNotNull(brands)) {
                for (Brand brand : brands) {
                    brandsInfo.put(brand.getId().toString(), brand.getName());
                }

                brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.SUCCESS.getCode()).brandMerchantResp(brandsInfo).message(OpsDashboardResponseCode.SUCCESS.getMessage()).status(Status.SUCCESS.getStatus()).build();
                return brandMerchantResponse;
            } else {
                brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.BRAND_NOT_FOUND.getCode()).message(OpsDashboardResponseCode.BRAND_NOT_FOUND.getMessage()).status(Status.FAILED.getStatus()).build();
                return brandMerchantResponse;
            }
        }
    }

    public ProductInfoResponse getProductsInfo(String brandId) {
        ProductInfoResponse productInfoResponse;
        try {
            List<ProductInfos> productsInfo = brandProductBO.getProductsInfo(brandId);
            LOGGER.info("ProductsInfo: {}", productsInfo);
            productInfoResponse = ProductInfoResponse.builder().productInfo(productsInfo).message(OpsDashboardResponseCode.SUCCESS.getMessage()).code(OpsDashboardResponseCode.SUCCESS.getCode()).status(Status.SUCCESS.getStatus()).build();
            return productInfoResponse;
        } catch (Exception e) {
            productInfoResponse = ProductInfoResponse.builder().code(OpsDashboardResponseCode.INVALID_PRODUCT.getCode()).message(OpsDashboardResponseCode.INVALID_PRODUCT.getMessage()).status(Status.FAILED.getStatus()).build();
            return productInfoResponse;
        }
    }

    public BrandMerchantResponse getMerchantInfo(String displayIdOrName) {
        BrandMerchantResponse brandMerchantResponse;
        if (StringUtils.hasText(displayIdOrName)) {
            try {
                HashMap<String, String> merchantUserInfo = merchantUserBO.getUserByNameOrDisplayId(displayIdOrName);
                LOGGER.info("Merchant_User_Info: {}", merchantUserInfo);
                brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.SUCCESS.getCode()).brandMerchantResp(merchantUserInfo).message(OpsDashboardResponseCode.SUCCESS.getMessage()).status(Status.SUCCESS.getStatus()).build();
                return brandMerchantResponse;
            } catch (Exception e) {
                brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.INVALID_MERCHANT.getCode()).message(OpsDashboardResponseCode.INVALID_MERCHANT.getMessage()).status(Status.FAILED.getStatus()).build();
                return brandMerchantResponse;
            }
        } else {
            brandMerchantResponse = BrandMerchantResponse.builder().code(OpsDashboardResponseCode.INVALID_MERCHANT.getCode()).message(OpsDashboardResponseCode.INVALID_MERCHANT.getMessage()).status(Status.FAILED.getStatus()).build();
            return brandMerchantResponse;
        }
    }
}
