package com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.TransactionCode;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class IsSubventionGivenAndSerialNumberVerified implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsSubventionGivenAndSerialNumberVerified.class);

    @Autowired
    public IsSubventionGivenAndSerialNumberVerified() {
    }

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        PgTransactionRequest request = (PgTransactionRequest) obj;
        if (BooleanUtils.isTrue(request.getIsSubvention())) {
            if (!CollectionUtils.isEmpty(request.getProducts()) && StringUtils.hasText(request.getProducts().get(0).getBrandId())) {
                if (BooleanUtils.isFalse(request.getProducts().get(0).getIsSerialNumberVerified())) {
                    LOGGER.error("Subvention is given by merchant but serial number is not verified for transaction request: {}", request);
                    throw new FreewayCustomException(TransactionCode.FAILED_161);
                }
            }
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
