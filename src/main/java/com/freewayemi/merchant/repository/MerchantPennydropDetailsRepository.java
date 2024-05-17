package com.freewayemi.merchant.repository;


import com.freewayemi.merchant.entity.MerchantPennydropDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantPennydropDetailsRepository extends MongoRepository<MerchantPennydropDetails,String> {

    Optional<List<MerchantPennydropDetails>> findMerchantPennydropDetailsByMerchantId(String merchantId, Pageable pageable);

}
