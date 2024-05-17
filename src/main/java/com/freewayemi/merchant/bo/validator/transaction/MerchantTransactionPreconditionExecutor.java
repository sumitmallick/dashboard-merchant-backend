package com.freewayemi.merchant.bo.validator.transaction;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MerchantTransactionPreconditionExecutor implements MerchantReqParamPreconditions {

    private final MerchantTransactionPreconditions merchantTxnPreconditions;

    @Autowired
    public MerchantTransactionPreconditionExecutor(MerchantTransactionPreconditions merchantTxnPreconditions) {
        this.merchantTxnPreconditions = merchantTxnPreconditions;
    }

    @Override
    public void executeConditions(Object object1, Object object2, Boolean isSeamless, String transactionSource)
            throws FreewayException {
        List<MerchantReqParamPreconditions> preconditionsList = merchantTxnPreconditions.getPreconditions();
        for (MerchantReqParamPreconditions preconditions : preconditionsList) {
            preconditions.executeConditions(object1, object2, isSeamless, transactionSource);
        }
    }

}
