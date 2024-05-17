package com.freewayemi.merchant.bo.validator.conditions.refundpayouts;

import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.entity.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsRefundPayoutAllowed implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsRefundPayoutAllowed.class);

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        MerchantUser mu = (MerchantUser) obj;
        if (null != mu && null != mu.getParams() && (null == mu.getParams().getIsAllowRefundPayout() ||
                !mu.getParams().getIsAllowRefundPayout())) {
            LOGGER.error("For display id: {} refund payout allowed is: {}", mu.getDisplayId(),
                    mu.getParams().getIsAllowRefundPayout());
            throw new FreewayCustomException(TransactionCode.FAILED_117.getCode(),
                    TransactionCode.FAILED_117.getDashboardStatusMsg());
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
