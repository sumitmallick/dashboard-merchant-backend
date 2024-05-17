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
public class HdfcDcPaymentProviderConditions implements PaymentProviderConditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(HdfcDcPaymentProviderConditions.class);

    private final Float minAmount;
    private final Float maxAmount;
    private final List<Integer> tenures;

    @Autowired
    public HdfcDcPaymentProviderConditions(@Value("${hdfc.dc.emi.tenure.restriction.min.amount}") Float minAmount,
                                           @Value("${hdfc.dc.emi.tenure.restriction.max.amount}") Float maxAmount,
                                           @Value("${hdfc.dc.emi.tenure.restriction.dynamic.tenure}") List<Integer> tenures) {
        this.minAmount = Util.getFLoat(minAmount);
        this.maxAmount = Util.getFLoat(maxAmount);
        this.tenures = tenures;
    }

    @Override
    public boolean isTenureRestricted(Float pgAmount, Integer tenure, String cardType) {
        pgAmount = Util.getFLoat(pgAmount);
        if (CardTypeEnum.DEBIT.getCardType().equalsIgnoreCase(cardType) || CardTypeEnum.CARDLESS.getCardType().equalsIgnoreCase(cardType)) {
            if (pgAmount < minAmount) {
                LOGGER.info("For pgAmount: {} tenure: {} minimum transaction amount allowed is: {} hence tenure:{} will not be shown", pgAmount, tenure, minAmount, tenure);
                return true;
            }
            if (pgAmount >= minAmount && pgAmount < maxAmount) {
                LOGGER.info("For pgAmount: {} tenure: {} minimum transaction amount: {} and maximum transaction amount: {} tenures allowed are: {}", pgAmount, tenure, minAmount, maxAmount, tenures);
                return !tenures.contains(tenure);
            }
        }
        return false;
    }
}
