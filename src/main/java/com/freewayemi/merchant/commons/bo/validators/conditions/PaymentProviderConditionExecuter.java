package com.freewayemi.merchant.commons.bo.validators.conditions;

import com.freewayemi.merchant.commons.type.BankEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderConditionExecuter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderConditionExecuter.class);

    private final PaymentProviderConditions hdfcPaymentProviderConditions;
    private final PaymentProviderConditions kotakDcPaymentProviderConditions;
    private final Boolean isHdfcPaymentProviderTenureRestrictionEnable;
    private final Boolean isKotakPaymentProviderTenureRestrictionEnable;

    @Autowired
    public PaymentProviderConditionExecuter(@Qualifier("hdfcDcPaymentProviderConditions") PaymentProviderConditions hdfcPaymentProviderConditions,
                                            @Qualifier("kotakDcPaymentProviderConditions") PaymentProviderConditions kotakDcPaymentProviderConditions,
                                            @Value("${hdfc.payment.provider.tenure.restriction.enable}") Boolean isHdfcPaymentProviderTenureRestrictionEnable,
                                            @Value("${kotak.payment.provider.tenure.restriction.enable}") Boolean isKotakPaymentProviderTenureRestrictionEnable) {
        this.hdfcPaymentProviderConditions = hdfcPaymentProviderConditions;
        this.kotakDcPaymentProviderConditions = kotakDcPaymentProviderConditions;
        this.isHdfcPaymentProviderTenureRestrictionEnable = isHdfcPaymentProviderTenureRestrictionEnable;
        this.isKotakPaymentProviderTenureRestrictionEnable = isKotakPaymentProviderTenureRestrictionEnable;
    }

    public boolean executeTenureRestrictedCondition(Float pgAmount, BankEnum bank, Integer tenure, String cardType) {
        if (isHdfcPaymentProviderTenureRestrictionEnable && BankEnum.HDFC.getCode().equalsIgnoreCase(bank.getCode())) {
            return hdfcPaymentProviderConditions.isTenureRestricted(pgAmount, tenure, cardType);
        }
        if (isKotakPaymentProviderTenureRestrictionEnable && BankEnum.KKBK.getCode().equalsIgnoreCase(bank.getCode())) {
            return kotakDcPaymentProviderConditions.isTenureRestricted(pgAmount, tenure, cardType);
        }
        return false;
    }
}
