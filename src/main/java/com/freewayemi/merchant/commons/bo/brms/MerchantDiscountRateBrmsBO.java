package com.freewayemi.merchant.commons.bo.brms;

import com.freewayemi.merchant.commons.dto.MerchantDiscountRateResponse;
import org.apache.http.annotation.Obsolete;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MerchantDiscountRateBrmsBO {

    public Float getMerchantDiscountRate(List<MerchantDiscountRateResponse> mdrs, String bankCode, String cardType,
                                         Integer tenure, String productId, String merchantId, String brandProductId) {
        if (CollectionUtils.isEmpty(mdrs)) {
            return null;
        }
        for (MerchantDiscountRateResponse mdr : mdrs) {
            if (bankCode.equals(mdr.getBankCode()) && cardType.equals(mdr.getCardType()) &&
                    tenure.equals(mdr.getTenure()) &&
                    isProductCriteria(brandProductId, mdr.getProductId(), mdr.getProductIds())) {
                return mdr.getRate() / 100;
            }
        }

        String pdt = null == productId ? "" : productId;
        List<MerchantDiscountRateResponse> out = mdrs.stream()
                .peek(mdr -> mdr.setScore(getScore(mdr, bankCode, cardType, tenure, productId, merchantId)))
                .collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(MerchantDiscountRateResponse::getScore).reversed())
                .map(mdr -> MerchantDiscountRateResponse.builder()
                        .brandId(null == mdr.getBrandId() ? pdt : mdr.getBrandId())
                        .cardType(null == mdr.getCardType() ? cardType : mdr.getCardType())
                        .bankCode(null == mdr.getBankCode() ? bankCode : mdr.getBankCode())
                        .tenure(null == mdr.getTenure() || mdr.getTenure().equals(-1) ? tenure : mdr.getTenure())
                        .productId(null == mdr.getProductId() ? brandProductId : mdr.getProductId())
                        .productIds(null == mdr.getProductIds() ? Collections.singletonList(brandProductId) :
                                mdr.getProductIds())
                        .rate(mdr.getRate())
                        .merchantId(null != mdr.getMerchantId() ? merchantId : mdr.getMerchantId())
                        .build())
                .filter(mdr -> (null == mdr.getCardType() && null == cardType) ||
                        mdr.getCardType().equals(cardType))
                .filter(mdr -> (null == mdr.getBankCode() && null == bankCode) ||
                        mdr.getBankCode().equals(bankCode))
                .filter(mdr -> (null == mdr.getProductId() && null == brandProductId) ||
                        mdr.getProductId().equals("any") ||
                        mdr.getProductId().equals(brandProductId))
                .filter(mdr -> (null == mdr.getProductIds() && null == brandProductId) ||
                        mdr.getProductIds().contains(brandProductId))
                .filter(mdr -> (null == mdr.getTenure() && null == tenure) ||
                        mdr.getTenure().equals(tenure))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return null;
        }
        return out.get(0).getRate() / 100;
    }

    private static Integer getScore(MerchantDiscountRateResponse mdr, String bankCode, String cardType, Integer tenure,
                                    String productId, String merchantId) {
        int score = 0;
        if (tenure.equals(mdr.getTenure())) {
            score += 1;
        }
        if (cardType.equals(mdr.getCardType())) {
            score += 1;
        }
        if (bankCode.equals(mdr.getBankCode())) {
            score += 1;
        }
        if (null != productId && productId.equals(mdr.getProductId())) {
            score += 1;
        }
        if (null != merchantId && merchantId.equals(mdr.getMerchantId())) {
            score += 1;
        }
        return score;
    }

    @Obsolete
    private boolean isProductCriteriaOld(String productId, String brandProductId, List<String> brandProductIds) {
        return (StringUtils.isEmpty(productId) ||
                (StringUtils.hasText(productId) && StringUtils.hasText(brandProductId) &&
                        "any".equals(brandProductId)) ||
                (StringUtils.hasText(productId) && null != brandProductIds && brandProductIds.size() > 0 &&
                        brandProductIds.contains(productId)));
    }

    private boolean isProductCriteria(String brandProductId, String offerProductId, List<String> brandProductIds) {
        if (StringUtils.hasText(brandProductId) && null != brandProductIds && brandProductIds.size() > 0) {
            return brandProductIds.contains(brandProductId);
        } else if (StringUtils.hasText(brandProductId) && StringUtils.hasText(offerProductId)) {
            return "any".equals(offerProductId);
        }
        else return StringUtils.isEmpty(brandProductId) && StringUtils.isEmpty(offerProductId) && CollectionUtils.isEmpty(brandProductIds);
    }
}
