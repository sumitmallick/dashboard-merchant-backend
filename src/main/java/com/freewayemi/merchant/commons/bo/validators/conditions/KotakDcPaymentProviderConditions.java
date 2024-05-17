package com.freewayemi.merchant.commons.bo.validators.conditions;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KotakDcPaymentProviderConditions implements PaymentProviderConditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(KotakDcPaymentProviderConditions.class);

    private final Float minTxnAmountFor3K;
    private final Float maxTxnAmountFor5K;
    private final List<Integer> tenuresFor3KTo5K;

    private final Float minTxnAmountFor5K;
    private final Float maxTxnAmountFor8K;
    private final List<Integer> tenuresFor5KTo8K;


    @Autowired
    public KotakDcPaymentProviderConditions(@Value("${kotak.dc.emi.tenure.restriction.min.amount.3k}") Float minTxnAmountFor3K,
                                            @Value("${kotak.dc.emi.tenure.restriction.max.amount.5k}") Float maxTxnAmountFor5K,
                                            @Value("${kotak.dc.emi.tenure.restriction.dynamic.tenure.3k.to.5k}") List<Integer> tenuresFor3KTo5K,
                                            @Value("${kotak.dc.emi.tenure.restriction.min.amount.5k}") Float minTxnAmountFor5K,
                                            @Value("${kotak.dc.emi.tenure.restriction.max.amount.8k}") Float maxTxnAmountFor8K,
                                            @Value("${kotak.dc.emi.tenure.restriction.dynamic.tenure.5k.to.8k}") List<Integer> tenuresFor5KTo8K) {
        this.minTxnAmountFor3K = minTxnAmountFor3K;
        this.maxTxnAmountFor5K = maxTxnAmountFor5K;
        this.tenuresFor3KTo5K = tenuresFor3KTo5K;
        this.minTxnAmountFor5K = minTxnAmountFor5K;
        this.maxTxnAmountFor8K = maxTxnAmountFor8K;
        this.tenuresFor5KTo8K = tenuresFor5KTo8K;
    }

    @Override
    public boolean isTenureRestricted(Float pgAmount, Integer tenure, String cardType) {
        pgAmount = Util.getFLoat(pgAmount);
        if (CardTypeEnum.DEBIT.getCardType().equalsIgnoreCase(cardType) || CardTypeEnum.CARDLESS.getCardType().equalsIgnoreCase(cardType)) {
            if (pgAmount < minTxnAmountFor3K) {
                LOGGER.info("For pgAmount: {} tenure: {} minimum transaction amount allowed is: {} hence tenure:{} will not be shown", pgAmount, tenure, minTxnAmountFor3K, tenure);
                return true;
            }
            if (pgAmount >= minTxnAmountFor3K && pgAmount < maxTxnAmountFor5K) {
                LOGGER.info("For pgAmount: {} tenure: {} minimum transaction amount: {} and maximum transaction amount: {} tenures allowed are: {}", pgAmount, tenure, minTxnAmountFor3K, maxTxnAmountFor5K, tenuresFor3KTo5K);
                return !tenuresFor3KTo5K.contains(tenure);
            }
            if (pgAmount >= minTxnAmountFor5K && pgAmount < maxTxnAmountFor8K) {
                LOGGER.info("For pgAmount: {} tenure: {} minimum transaction amount: {} and maximum transaction amount: {} tenures allowed are: {}", pgAmount, tenure, minTxnAmountFor5K, maxTxnAmountFor8K, tenuresFor5KTo8K);
                return !tenuresFor5KTo8K.contains(tenure);
            }
        }
        return false;
    }
}
