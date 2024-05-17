package com.freewayemi.merchant.commons.bo.brms;

import com.freewayemi.merchant.bo.OfferBO;
import com.freewayemi.merchant.commons.dto.OfferDetails;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.controller.MerchantEligibilityController;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OfferBrmsBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferBrmsBO.class);

    public List<Output> calculateOffers(Input input) {
        List<Output> offerLists = new ArrayList<>();
        // Single default offer
        Output output = calculate(input);
        if (Util.isNotNull(output)) {
            offerLists.add(output);
        }
        List<Output> outputList = findEffectiveTenureOffers(input);
        if (!CollectionUtils.isEmpty(outputList)) {
            offerLists.addAll(outputList);
        }
        return offerLists;
    }

    private List<Output> findEffectiveTenureOffers(Input input) {
        List<Output> outputs = new ArrayList<>();
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) || BooleanUtils.isFalse(input.getIsSubvented())) {
            return outputs;
        }
        List<OfferResponse> effectiveTenureOffers = input.getOffers().stream().filter(offerResponse -> null != offerResponse.getEffectiveTenure() && offerResponse.getEffectiveTenure() > 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(effectiveTenureOffers)) {
            return outputs;
        }
        return effectiveTenureOffers.stream()
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() && offerResponse.getEffectiveTenure() > 0)
                .filter(offerResponse ->
                        (null == offerResponse.getType() || !offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback")))
                .filter(offerResponse -> input.getBankCode().equals(offerResponse.getBankCode())
                        && input.getCardType().equals(offerResponse.getCardType())
                        && input.getTenure().equals(offerResponse.getTenure())
                        && isValidTxnAmount(input.getTxnAmount(), offerResponse.getMinAmount()))
                .filter(offerResponse -> (null == offerResponse.getProductId() && null == input.getProductId()) ||
                        offerResponse.getProductId().equals(input.getProductId())).map(offerResponse ->
                        new Output(offerResponse.getSubvention() / 100, 0f, offerResponse.getEffectiveTenure(), offerResponse.getOfflineAdvanceEmiTenure()))
                .collect(Collectors.toList());

//        if (null == input.getProductId()) {
//            for (OfferResponse offer : effectiveTenureOffers) {
//                if (null != offer.getEffectiveTenure() && offer.getEffectiveTenure() > 0 &&
//                        (null == offer.getType() || !offer.getType().equalsIgnoreCase("brandBankAdditionalCashback")) &&
//                        input.getBankCode().equals(offer.getBankCode()) &&
//                        input.getCardType().equals(offer.getCardType()) &&
//                        input.getTenure().equals(offer.getTenure()) &&
//                        isValidTxnAmount(input.getTxnAmount(), offer.getMinAmount())) {
//                    outputs.add(new Output(offer.getSubvention() / 100, offer.getEffectiveTenure());
//                }
//            }
//        }
    }

    public Output calculate(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) || BooleanUtils.isFalse(input.getIsSubvented())) {
            return new Output(0.0f);
        }

        if (null == input.getProductId()) {
            for (OfferResponse offer : input.getOffers()) {
                if ((null == offer.getType() || !offer.getType().
                        equalsIgnoreCase("brandBankAdditionalCashback")) &&
                        input.getBankCode().equals(offer.getBankCode()) &&
                        input.getCardType().equals(offer.getCardType()) &&
                        input.getTenure().equals(offer.getTenure()) &&
                        isValidTxnAmount(input.getTxnAmount(), offer.getMinAmount()) &&
                        isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure())) {
                    return new Output(offer.getSubvention() / 100, 0f, 0, offer.getOfflineAdvanceEmiTenure());
                }
            }
        }

        List<OfferResponse> out = input.getOffers().stream()
                .map(offer -> offer.setScore(getScore(offer, input))).collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer ->
                        new OfferResponse(
                                offer.getId(),
                                null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure() :
                                        offer.getTenure(),
                                offer.getSubvention(),
                                offer.getActive(),
                                null == offer.getCardType() ? input.getCardType() : offer.getCardType(),
                                null == offer.getProductId() ? input.getProductId() : offer.getProductId(),
                                null,
                                null == offer.getBankCode() ? input.getBankCode() : offer.getBankCode(),
                                offer.getValidFrom(),
                                offer.getValidTo(),
                                null == offer.getMinAmount() ? input.getTxnAmount() : offer.getMinAmount(),
                                null, null, null, null,
                                null, null, null, null, offer.getVelocity(),
                                offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                                offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                                offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount())
                )
                .filter(offer -> null == offer.getType() || !offer.getType().
                        equalsIgnoreCase("brandBankAdditionalCashback"))
                .filter(offer -> (null == offer.getCardType() && null == input.getCardType()) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (null == offer.getBankCode() && null == input.getBankCode()) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> (null == offer.getProductId() && null == input.getProductId()) ||
                        offer.getProductId().equals(input.getProductId()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure()) ||
                        offer.getTenure().equals(input.getTenure()))
                .filter(offer -> isValidTxnAmount(input.getTxnAmount(), offer.getMinAmount()))
                .filter(offer -> isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return null;
        }

        return new Output(out.get(0).getSubvention() / 100, 0f, 0, out.get(0).getOfflineAdvanceEmiTenure());
    }

    private Integer getScore(OfferResponse offer, Input input) {
        int score = 0;
        if (input.getTenure().equals(offer.getTenure()))
            score += 1;
        if (input.getCardType().equals(offer.getCardType()))
            score += 1;
        if (input.getBankCode().equals(offer.getBankCode()))
            score += 1;
        if (null != input.getProductId() && input.getProductId().equals(offer.getProductId()))
            score += 1;
        if (null != offer.getMinAmount() && null != input.getTxnAmount() && Util.getFLoat(input.getTxnAmount()) >= Util.getFLoat(offer.getMinAmount()))
            score += 1;
        if (null != offer.getMaxAmount() && null != input.getTxnAmount() && Util.getFLoat(input.getTxnAmount()) <= Util.getFLoat(offer.getMaxAmount()))
            score += 1;
        return score;
    }

    private Integer getScoreWithMinMax(OfferResponse offer) {
        int score = 0;
        if(Objects.nonNull(offer.getMinAmount())){
            score += 1;
        }
        if(Objects.nonNull(offer.getMaxAmount())){
            score += 1;
        }
        return score;
    }

    private boolean isValidTxnAmount(Float txnAmount, Float minimumOfferAmount) {
        if (null != minimumOfferAmount)
            return null != txnAmount && Util.getFLoat(txnAmount) >= Util.getFLoat(minimumOfferAmount);
        else return true;
    }

    private static boolean isValidEffectiveEmiTenure(Integer inputEffectiveTenure, Integer offerEffectiveTenure) {
        return null == inputEffectiveTenure || inputEffectiveTenure <= 0 || inputEffectiveTenure.equals(offerEffectiveTenure);
    }

    public OfferDetails calculateV3(Input input) {
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) || BooleanUtils.isFalse(input.getIsSubvented())) {
            OfferDetails offerDetails = new OfferDetails("", 0.0f, 0.0f,
                    0.0f, null);
//            offerDetails.setOfferPercentage(subvention / 100);
            return offerDetails;
//            return new Output(0.0f);
        }
        List<OfferResponse> minMaxOffers = input.getOffers().stream()
                .map(offer -> offer.setScore(getScoreWithMinMax(offer))).collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .collect(Collectors.toList());

        if (null == input.getProductId()) {
            for (OfferResponse offer : minMaxOffers) {
                if ((null == offer.getType() || !offer.getType().
                        equalsIgnoreCase("brandBankAdditionalCashback")) &&
                        input.getBankCode().equals(offer.getBankCode()) &&
                        input.getCardType().equals(offer.getCardType()) &&
                        input.getTenure().equals(offer.getTenure()) &&
                        isValidTxnAmount(input.getTxnAmount(), offer.getMinAmount()) &&
                        isNonEffectiveEmiTenure(offer.getEffectiveTenure()) &&
                        isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()) &&
                        isValidAmountRange(offer.getMinAmount(), offer.getMaxAmount(), input.getTxnAmount())) {

                    OfferDetails offerDetails = new OfferDetails(offer.getId(), ((offer.getSubvention() / 100)* input.getTxnAmount()), 0.0f,
                            0.0f, offer);
                    offerDetails.setOfferRate(offer.getSubvention() / 100);
                    return offerDetails;
                }
            }
        }

        List<OfferResponse> out = input.getOffers().stream()
                .map(offer -> offer.setScore(getScore(offer, input))).collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .map(offer ->
                        new OfferResponse(
                                offer.getId(),
                                null == offer.getTenure() || offer.getTenure().equals(-1) ? input.getTenure() :
                                        offer.getTenure(),
                                offer.getSubvention(),
                                offer.getActive(),
                                StringUtils.isEmpty(offer.getCardType()) ? input.getCardType() : offer.getCardType(),
                                null == offer.getProductId() ? input.getProductId() : offer.getProductId(),
                                null,
                                StringUtils.isEmpty(offer.getBankCode()) ? input.getBankCode() : offer.getBankCode(),
                                offer.getValidFrom(),
                                offer.getValidTo(),
                                null == offer.getMinAmount() ? input.getTxnAmount() : offer.getMinAmount(),
                                null, null, null, null,
                                null, null, null, null,
                                offer.getVelocity(), offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                                offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                                offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount())
                )
                .filter(offer -> null == offer.getType() || !offer.getType().
                        equalsIgnoreCase("brandBankAdditionalCashback"))
                .filter(offer -> (StringUtils.isEmpty(offer.getCardType()) && StringUtils.isEmpty(input.getCardType())) ||
                        offer.getCardType().equals(input.getCardType()))
                .filter(offer -> (StringUtils.isEmpty(offer.getBankCode()) && StringUtils.isEmpty(input.getBankCode())) ||
                        offer.getBankCode().equals(input.getBankCode()))
                .filter(offer -> (null == offer.getProductId() && null == input.getProductId()) ||
                        offer.getProductId().equals(input.getProductId()))
                .filter(offer -> (null == offer.getTenure() && null == input.getTenure()) ||
                        offer.getTenure().equals(input.getTenure()))
                .filter(offer -> isValidTxnAmount(input.getTxnAmount(), offer.getMinAmount()))
                .filter(offer -> isNonEffectiveEmiTenure(offer.getEffectiveTenure()))
                .filter(offer -> isValidEffectiveEmiTenure(input.getEffectiveTenure(), offer.getEffectiveTenure()))
                .filter(offer -> isValidAmountRange(offer.getMinAmount(), offer.getMaxAmount(), input.getTxnAmount()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return null;
        }

        OfferDetails offerDetails = new OfferDetails(out.get(0).getId(), ((out.get(0).getSubvention() / 100)* input.getTxnAmount()), 0.0f,
                0.0f, out.get(0));
        offerDetails.setOfferRate(out.get(0).getSubvention() / 100);
        return offerDetails;
    }


    public List<OfferDetails> calculateMerchantOffers(Input input) {
        List<OfferDetails> offerLists = new ArrayList<>();
        // Single default offer
        OfferDetails offerDetail = calculateV3(input);
        if (Util.isNotNull(offerDetail)) {
            offerLists.add(offerDetail);
        }
        List<OfferDetails> offerDetailsList = calculateEffectiveTenureOffers(input);
        if (!CollectionUtils.isEmpty(offerDetailsList)) {
            offerLists.addAll(offerDetailsList);
        }
        return offerLists;
    }

    private List<OfferDetails> calculateEffectiveTenureOffers(Input input) {
        List<OfferDetails> outputs = new ArrayList<>();
        if (CardTypeEnum.BNPL.name().equalsIgnoreCase(input.getCardType()) || BooleanUtils.isFalse(input.getIsSubvented())) {
            return outputs;
        }
        List<OfferResponse> effectiveTenureOffers = input.getOffers().stream().filter(offerResponse -> null != offerResponse.getEffectiveTenure() && offerResponse.getEffectiveTenure() > 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(effectiveTenureOffers)) {
            return outputs;
        }
        List<OfferResponse> minMaxOffers = input.getOffers().stream()
                .map(offer -> offer.setScore(getScoreWithMinMax(offer))).collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(OfferResponse::getScore).reversed())
                .collect(Collectors.toList());

        List<OfferResponse> offerResponses = minMaxOffers.stream()
                .filter(offerResponse -> null != offerResponse.getEffectiveTenure() && offerResponse.getEffectiveTenure() > 0)
                .filter(offerResponse ->
                        (null == offerResponse.getType() || !offerResponse.getType().equalsIgnoreCase("brandBankAdditionalCashback")))
                .filter(offerResponse -> input.getBankCode().equals(offerResponse.getBankCode())
                        && input.getCardType().equals(offerResponse.getCardType())
                        && input.getTenure().equals(offerResponse.getTenure())
                        && isValidTxnAmount(input.getTxnAmount(), offerResponse.getMinAmount()))
                .filter(offerResponse -> (null == offerResponse.getProductId() && null == input.getProductId()) || null==offerResponse.getProductId() ||
                        offerResponse.getProductId().equals(input.getProductId()))
                .filter(offerResponse -> isValidAmountRange(offerResponse.getMinAmount(), offerResponse.getMaxAmount(), input.getTxnAmount() - Util.calculateFixedDownPayment(input.getTxnAmount(), offerResponse.getTenure(), offerResponse.getEffectiveTenure())))
                .collect(Collectors.toList());

        for (OfferResponse offerResponse : offerResponses) {
            OfferDetails offerDetails = new OfferDetails(offerResponse.getId(), 0.0f, 0.0f, 0.0f, offerResponse);
            offerDetails.setOfferRate(offerResponse.getSubvention() / 100);
            outputs.add(offerDetails);
        }
        return outputs;

    }

    private static boolean isNonEffectiveEmiTenure(Integer offerEffectiveTenure) {
        return null == offerEffectiveTenure || offerEffectiveTenure <= 0;
    }

    private static boolean isValidAmountRange(Float minAmount, Float maxAmount, Float transactionAmount){
        if(Objects.nonNull(minAmount) && transactionAmount < minAmount){
            return false;
        }
        if(Objects.nonNull(maxAmount) && transactionAmount > maxAmount){
            return false;
        }
        return true;
    }
}
