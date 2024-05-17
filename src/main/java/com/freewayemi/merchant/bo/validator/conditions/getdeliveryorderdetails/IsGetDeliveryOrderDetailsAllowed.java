package com.freewayemi.merchant.bo.validator.conditions.getdeliveryorderdetails;

import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.entity.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsGetDeliveryOrderDetailsAllowed implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsGetDeliveryOrderDetailsAllowed.class);

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        MerchantUser mu = (MerchantUser) obj;
        if (null != mu && null != mu.getParams() && (null == mu.getParams().getIsGetDeliveryOrderDetailsAllowed() ||
                !mu.getParams().getIsGetDeliveryOrderDetailsAllowed())) {
            LOGGER.error("For display id: {} refund payout allowed is: {}", mu.getDisplayId(),
                    mu.getParams().getIsGetDeliveryOrderDetailsAllowed());
            throw new FreewayCustomException(TransactionCode.FAILED_145.getCode(),
                    TransactionCode.FAILED_145.getDashboardStatusMsg());
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
