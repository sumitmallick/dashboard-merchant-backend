package com.freewayemi.merchant.commons.transaction.precondition;

import com.freewayemi.merchant.commons.exception.FreewayException;

public interface MerchantReqParamPreconditions {

    void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException;

}
