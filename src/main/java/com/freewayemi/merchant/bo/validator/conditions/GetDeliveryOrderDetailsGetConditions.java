package com.freewayemi.merchant.bo.validator.conditions;

import com.freewayemi.merchant.commons.bo.validators.conditions.GetConditions;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.bo.validator.conditions.getdeliveryorderdetails.IsGetDeliveryOrderDetailsAllowed;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDeliveryOrderDetailsGetConditions implements GetConditions {

    private final List<Conditions> conditionsList;

    @Autowired
    public GetDeliveryOrderDetailsGetConditions(IsGetDeliveryOrderDetailsAllowed isGetDeliveryOrderDetailsAllowed) {
        this.conditionsList = ImmutableList.of(isGetDeliveryOrderDetailsAllowed);
    }

    @Override
    public List<Conditions> getConditionsList() {
        return conditionsList;
    }
}
