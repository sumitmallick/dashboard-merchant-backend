package com.freewayemi.merchant.commons.transaction.precondition;

import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayException;

public interface TransactionPostConditions {
	
	void executeConditions(String paymentTxnId, RefundTransactionRequest refundTxnRequest) throws FreewayException;

}
