package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.BrandMerchantDataResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.response.BrandResponse;
import com.freewayemi.merchant.entity.BrandMerchantData;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.BrandMerchantDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BrandMerchantDataBO {
    private final Logger LOGGER = LoggerFactory.getLogger(BrandMerchantDataBO.class);
    private final BrandMerchantDataRepository brandMerchantDataRepository;
    private final MerchantUserBO merchantUserBO;
    private final BrandBO brandBO;

    @Autowired
    public BrandMerchantDataBO(BrandMerchantDataRepository brandProductInventoryRepository, MerchantUserBO merchantUserBO, BrandBO brandBO) {
        this.brandMerchantDataRepository = brandProductInventoryRepository;
        this.merchantUserBO = merchantUserBO;
        this.brandBO = brandBO;
    }

    public BrandMerchantDataResponse searchBrandData(String brandId, String gst, String merchantId) {
        List<BrandMerchantData> brandMerchantDataList;
        if (StringUtils.hasText(merchantId)) {
            brandMerchantDataList = brandMerchantDataRepository.findByBrandIdAndGstAndMerchantId(brandId, gst, merchantId).orElse(new ArrayList<>());
        } else {
            brandMerchantDataList = brandMerchantDataRepository.findByBrandIdAndGst(brandId, gst).orElse(new ArrayList<>());
        }
        LOGGER.info("baaa: {} ", brandMerchantDataList);
        if (brandMerchantDataList.size() > 0) {
            BrandMerchantData brandMerchantData = brandMerchantDataList.stream().findFirst().orElse(new BrandMerchantData());
            return BrandMerchantDataResponse.builder().brandId(brandMerchantData.getBrandId()).gst(brandMerchantData.getGst()).storeCode(brandMerchantData.getStoreCode()).distributorCode(brandMerchantData.getDistributorCode()).merchantId(brandMerchantData.getMerchantId()).build();
        }
        return BrandMerchantDataResponse.builder().build();
    }

    public List<BrandMerchantDataResponse> getBrandsForMerchant(String displayMerchantId, String merchantId) {
        MerchantUser mu = null;
        try {
            if (null != merchantId && StringUtils.hasText(merchantId)) {
                LOGGER.info("Getting merchantUser for merchantId: {}", merchantId);
                mu = merchantUserBO.getUserByMerchantIdOrDisplayId(merchantId);
            }
            if (null != displayMerchantId && StringUtils.hasText(displayMerchantId)) {
                LOGGER.info("Getting merchantUser for displayMerchantId: {}", displayMerchantId);
                mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
            }
            if (Objects.isNull(mu)) {
                LOGGER.error(String.format("No merchant found for displayMerchantId: %s and merchantId: %s", displayMerchantId, merchantId));
                throw new FreewayException("No merchantUser Found!!");
            }
            List<BrandResponse> brandResponses = brandBO.get(mu, null);
            return buildBrandMerchantDataResponse(brandResponses);
        } catch (Exception e) {
            LOGGER.error(String.format("Exception occurred while getting merchant for displayMerchantId: %s and merchantId: %s", displayMerchantId, merchantId), e);
            throw new FreewayException("Exception occurred while getting merchantUser!");
        }
    }

    private List<BrandMerchantDataResponse> buildBrandMerchantDataResponse(List<BrandResponse> brands) {
        List<BrandMerchantDataResponse> responses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brands)) {
            for (BrandResponse brand : brands) {
                responses.add(BrandMerchantDataResponse.builder()
                        .brandId(brand.getBrandId())
                        .displayBrandId(brand.getBrandDisplayId())
                        .build());
            }
        }
        return responses;
    }

    public List<BrandMerchantData> getBrandMerchantDatas(String gst){
        return brandMerchantDataRepository.findByGst(gst).orElse(new ArrayList<>());
    }
}
