package com.freewayemi.merchant.bo.validator.transaction.preconditions;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsValidAmount implements MerchantReqParamPreconditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsValidAmount.class);

    @Override
    public void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException {
        String orderId = ((PgTransactionRequest) object2).getOrderId();
        Float amt = ((PgTransactionRequest) object2).getAmount();
        String amount = String.valueOf(((PgTransactionRequest) object2).getAmount());

        if (amt < 2000.0f) {
            LOGGER.error("Minimum transaction amount should be 2000.0");
            throw new FreewayException("Minimum transaction amount should be 2000.0");
        }
        if (!ValidationUtil.validateAmount(amount)) {
            LOGGER.error("Invalid amount format for orderId: {} and amount: {}", orderId, amount);
            throw new FreewayException("Invalid amount. Pass amount with integer-part from 1 to 9,"
                    + " with max seven digit followed by two fractional-part e.g. 9999999.99");
        }
    }

}
