package com.freewayemi.merchant.bo.validator.conditions;

import com.freewayemi.merchant.commons.bo.validators.conditions.GetConditions;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.bo.validator.conditions.refundpayouts.IsRefundPayoutAllowed;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RefundPayoutsGetConditions implements GetConditions {

    private final List<Conditions> conditionsList;

    @Autowired
    public RefundPayoutsGetConditions(IsRefundPayoutAllowed isRefundPayoutAllowed) {
        this.conditionsList = ImmutableList.of(isRefundPayoutAllowed);
    }

    @Override
    public List<Conditions> getConditionsList() {
        return conditionsList;
    }
}
