package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandGst;
import com.freewayemi.merchant.entity.MerchantSessionActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantSessionActivityRepository extends MongoRepository<MerchantSessionActivity,String> {
}
