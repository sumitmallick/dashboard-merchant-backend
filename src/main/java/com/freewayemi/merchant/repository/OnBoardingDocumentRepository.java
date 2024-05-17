package com.freewayemi.merchant.repository;

import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.BrandGst;
import com.freewayemi.merchant.entity.OnBoardingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OnBoardingDocumentRepository extends MongoRepository<OnBoardingDocument,String> {
    Optional<List<OnBoardingDocument>> findByPartnerAndBusinessType(String partner, String businessType);

    @Query("{ 'partner':?0  , 'businessType': ?1, 'isMandatory.onBoarding': {$exists:  ?2}}")
    Optional<List<OnBoardingDocument>> findByPartnerAndBusinessTYpeForOnboarding(String partner, String businessType, Boolean onBoarding);

    @Query("{ 'partner':?0  , 'businessType': ?1, 'isMandatory.settlement' : ?2}")
    Optional<List<OnBoardingDocument>> findByPartnerAndBusinessTYpeForSettlement(String partner, String businessType, Boolean settlement);
}
