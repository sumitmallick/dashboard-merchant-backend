package com.freewayemi.merchant.commons.bo.eligibility.provider;

import com.freewayemi.merchant.commons.bo.eligibility.EligibilityRequest;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;

public interface EligibilityProvider {
    EligibilityResponse check(EligibilityRequest request);
    PaymentProviderEnum getProvider();
    String getBankCode();
    int getScore();
    String getCardType();
}
