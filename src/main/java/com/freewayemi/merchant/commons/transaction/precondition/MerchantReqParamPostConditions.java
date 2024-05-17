package com.freewayemi.merchant.commons.transaction.precondition;

import com.freewayemi.merchant.commons.exception.FreewayException;

public interface MerchantReqParamPostConditions {

	void executeConditions(Object obj1, Object obj2) throws FreewayException;

}
