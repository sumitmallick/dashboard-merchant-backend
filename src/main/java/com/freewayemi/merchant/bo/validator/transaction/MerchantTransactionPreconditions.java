package com.freewayemi.merchant.bo.validator.transaction;

import com.freewayemi.merchant.commons.transaction.precondition.MerchantReqParamPreconditions;
import com.freewayemi.merchant.commons.transaction.precondition.TransactionConditions;
import com.freewayemi.merchant.bo.validator.transaction.preconditions.DisableOnlineRequest;
import com.freewayemi.merchant.bo.validator.transaction.preconditions.IsValidAmount;
import com.freewayemi.merchant.bo.validator.transaction.preconditions.IsValidMobile;
import com.freewayemi.merchant.bo.validator.transaction.preconditions.IsValidOrderId;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MerchantTransactionPreconditions implements TransactionConditions<MerchantReqParamPreconditions> {

    private final List<MerchantReqParamPreconditions> preconditions;

    @Autowired
    public MerchantTransactionPreconditions(IsValidAmount isValidAmt, IsValidMobile isValidMobile,
                                            IsValidOrderId isValidOrderId, DisableOnlineRequest disableOnlineRequest) {
        this.preconditions = ImmutableList.of(isValidAmt, isValidMobile, isValidOrderId, disableOnlineRequest);
    }

    @Override
    public List<MerchantReqParamPreconditions> getPreconditions() {
        return preconditions;
    }

}
