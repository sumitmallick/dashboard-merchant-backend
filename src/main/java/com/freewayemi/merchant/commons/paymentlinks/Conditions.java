package com.freewayemi.merchant.commons.paymentlinks;

import com.freewayemi.merchant.commons.exception.FreewayException;

public interface Conditions {

    void executeConditions(Object obj) throws FreewayException;

    void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException;

}
