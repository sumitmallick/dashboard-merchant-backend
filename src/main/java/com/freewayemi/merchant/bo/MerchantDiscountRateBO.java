package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.MerchantDiscountRateResponse;
import com.freewayemi.merchant.commons.entity.MerchantDiscountRate;
import com.freewayemi.merchant.repository.MerchantDiscountRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MerchantDiscountRateBO {

    private final MerchantDiscountRateRepository merchantDiscountRateRepository;

    @Autowired
    public MerchantDiscountRateBO(MerchantDiscountRateRepository merchantDiscountRateRepository) {
        this.merchantDiscountRateRepository = merchantDiscountRateRepository;
    }

    public List<MerchantDiscountRateResponse> getMerchantDiscountRateByBrandId(String brandId) {
        if (StringUtils.isEmpty(brandId))
            return new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.000");
        List<MerchantDiscountRate> merchantDiscountRates = merchantDiscountRateRepository.findByBrandId(brandId).orElse(null);
        return CollectionUtils.isEmpty(merchantDiscountRates) ? null :
                merchantDiscountRates.stream()
                        .map(mdr -> MerchantDiscountRateResponse.builder()
                                .id(mdr.getId().toString())
                                .merchantId(mdr.getMerchantId())
                                .rate(Util.getFLoat(mdr.getRate()))
                                .active(mdr.isActive())
                                .bankCode(mdr.getBankCode())
                                .brandId(mdr.getBrandId())
                                .cardType(mdr.getCardType())
                                .createdBy(mdr.getCreatedBy())
                                .productId(mdr.getProductId())
                                .productIds(mdr.getProductIds())
                                .score(mdr.getScore())
                                .tenure(mdr.getTenure())
                                .updatedBy(mdr.getUpdatedBy())
                                .build())
                        .collect(Collectors.toList());
    }
}
