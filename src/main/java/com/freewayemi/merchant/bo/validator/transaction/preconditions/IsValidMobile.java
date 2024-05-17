package com.freewayemi.merchant.bo.validator.transaction.preconditions;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IsValidMobile implements MerchantReqParamPreconditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsValidMobile.class);

    @Override
    public void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException {
        String orderId = ((PgTransactionRequest) object2).getOrderId();
        String mobile = String.valueOf(((PgTransactionRequest) object2).getMobile());

        if (!ValidationUtil.validateMobileNumber(mobile)) {
            LOGGER.error("Invalid mobile number format for orderId: {} and mobile: {}", orderId, mobile);
            throw new FreewayException("Invalid mobile. Pass mobile number with 10 digit e.g. 9123456789");
        }
        if (!ValidationUtil.isGenuineMobileNumber(mobile)) {
            LOGGER.error("Invalid mobile number for orderId: {} and mobile: {}", orderId, mobile);
            throw new FreewayException("Invalid mobile number. Send genuine mobile number");
        }
    }

}
