package com.freewayemi.merchant.commons.bo.validators.conditions;

public interface PaymentProviderConditions {
    public boolean isTenureRestricted(Float pgAmount, Integer tenure, String cardType);
}
