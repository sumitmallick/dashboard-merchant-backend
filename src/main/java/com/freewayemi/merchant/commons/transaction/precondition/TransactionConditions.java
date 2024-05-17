package com.freewayemi.merchant.commons.transaction.precondition;

import java.util.List;

public interface TransactionConditions<E> {

	public List<E> getPreconditions();

}
