package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.BrandGst;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;
public interface BrandGstRepository extends MongoRepository<BrandGst,String> {
    Optional<List<BrandGst>> findByGst(String gst);
}
