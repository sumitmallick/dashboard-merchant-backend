package com.freewayemi.merchant.bo.validator.conditions;

import com.freewayemi.merchant.commons.bo.validators.conditions.GetConditions;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink.*;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeamlessPaymentLinkGetConditions implements GetConditions {

    private final List<Conditions> conditionsList;

    @Autowired
    public SeamlessPaymentLinkGetConditions(IsBankCodeValid isBankCodeValid, IsCardTypeValid isCardTypeValid,
                                            IsTenureValid isTenureValid, IsEmiOptionValid isEmiOptionValid,
                                            IsSubventionGivenAndSerialNumberVerified isSubventionGivenAndSerialNumberVerified,
                                            IsValidProductModelOfBrand isValidProductModelOfBrand) {
        this.conditionsList = ImmutableList.of(isBankCodeValid, isCardTypeValid, isTenureValid, isEmiOptionValid, isSubventionGivenAndSerialNumberVerified, isValidProductModelOfBrand);
    }

    @Override
    public List<Conditions> getConditionsList() {
        return conditionsList;
    }
}
