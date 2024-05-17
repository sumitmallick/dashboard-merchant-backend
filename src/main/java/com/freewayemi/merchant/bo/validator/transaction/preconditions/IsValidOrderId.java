package com.freewayemi.merchant.bo.validator.transaction.preconditions;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsValidOrderId implements MerchantReqParamPreconditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsValidMobile.class);

    @Override
    public void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException {
        String orderId = String.valueOf(((PgTransactionRequest) object2).getOrderId());
        if (!ValidationUtil.validateOrderId(orderId)) {
            LOGGER.error("Invalid orderId format for orderId: {}", orderId);
            throw new FreewayException("Invalid orderId. Pass orderId with max 20 character having alphabets" +
                    " numbers and special character: hyphen or underscore");
        }
    }

}
