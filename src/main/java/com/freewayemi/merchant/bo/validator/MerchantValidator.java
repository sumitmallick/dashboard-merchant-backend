package com.freewayemi.merchant.bo.validator;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.bo.validator.transaction.MerchantTransactionPreconditionExecutor;
import com.freewayemi.merchant.entity.MerchantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MerchantValidator {

    private final MerchantTransactionPreconditionExecutor merTxnPreconditionExecutor;

    @Autowired
    public MerchantValidator(MerchantTransactionPreconditionExecutor merTxnPreconditionExecutor) {
        this.merTxnPreconditionExecutor = merTxnPreconditionExecutor;
    }

    public void validateV2(MerchantUser mu, PgTransactionRequest request, Boolean isSeamless,
                           String transactionSource) {
        merTxnPreconditionExecutor.executeConditions(mu, request, isSeamless, transactionSource);
    }

}
