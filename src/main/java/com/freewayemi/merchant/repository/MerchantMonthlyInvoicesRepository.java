package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantMonthlyInvoices;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MerchantMonthlyInvoicesRepository extends MongoRepository<MerchantMonthlyInvoices, String> {
    @Query(value = "{'merchantId' : ?0}", sort = "{createdDate : -1}")
    List<MerchantMonthlyInvoices> findByMerchantId(String merchantId);
}
