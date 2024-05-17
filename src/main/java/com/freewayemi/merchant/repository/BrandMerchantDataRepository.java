package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandMerchantData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BrandMerchantDataRepository extends MongoRepository<BrandMerchantData, String> {
    Optional<List<BrandMerchantData>> findByBrandIdAndGst(String brandId, String gst);
    Optional<List<BrandMerchantData>> findByBrandIdAndGstAndMerchantId(String brandId, String gst, String merchantId);
    Optional<List<BrandMerchantData>> findByGst(String gst);
}