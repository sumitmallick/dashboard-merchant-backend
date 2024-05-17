package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.MerchantHowToVideo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantHowToVideosRepository extends MongoRepository<MerchantHowToVideo, String> {
    Optional<List<MerchantHowToVideo>> findByType(String type);
}
