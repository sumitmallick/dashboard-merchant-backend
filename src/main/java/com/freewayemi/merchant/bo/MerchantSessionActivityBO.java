package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.MerchantProductRequest;
import com.freewayemi.merchant.dto.MerchantSessionActivityRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.entity.MerchantProduct;
import com.freewayemi.merchant.entity.MerchantSessionActivity;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.repository.CatalogProductRepository;
import com.freewayemi.merchant.repository.MerchantSessionActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MerchantSessionActivityBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSessionActivityBO.class);

    private MerchantSessionActivityRepository merchantSessionActivityRepository;
    private CatalogProductRepository catalogProductRepository;

    @Autowired
    public MerchantSessionActivityBO(MerchantSessionActivityRepository merchantSessionActivityRepository,
                                     CatalogProductRepository catalogProductRepository){
        this.merchantSessionActivityRepository = merchantSessionActivityRepository;
        this.catalogProductRepository = catalogProductRepository;
    }

    public BasicResponse saveMerchantSessionActivities(MerchantSessionActivityRequest merchantSessionActivityRequest){
        MerchantSessionActivity merchantSessionActivity = MerchantSessionActivity.builder().sessionId(merchantSessionActivityRequest.getSessionId())
                .paymentLinkId(merchantSessionActivityRequest.getPaymentLinkId())
                .type(merchantSessionActivityRequest.getType())
                .lastActivityDate(merchantSessionActivityRequest.getLastActivityDate())
                .build();
        merchantSessionActivityRepository.save(merchantSessionActivity);
        return BasicResponse.builder().statusMsg("successfully stored session activity").status(Status.SUCCESS).statusCode(0).build();
    }

    public BasicResponse createMerchantProduct(MerchantProductRequest merchantProductRequest){
        MerchantProduct merchantProduct = new MerchantProduct();
        merchantProduct.setMerchantId(merchantProductRequest.getMerchantId());
        merchantProduct.setActive(Boolean.TRUE);
        merchantProduct.setProductCategory(merchantProductRequest.getProductCategory());
        merchantProduct.setProductName(merchantProductRequest.getProductName());
        merchantProduct.setProductPrice(merchantProductRequest.getProductPrice());
        merchantProduct.setCreatedDate(Instant.now());
        merchantProduct.setLastModifiedDate(Instant.now());
        merchantProduct.setUuid(UUID.randomUUID().toString());
        catalogProductRepository.save(merchantProduct);
        return BasicResponse.builder().statusMsg("successfully created merchant product").status(Status.SUCCESS).statusCode(0).build();
    }
}
