package com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink;

import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.EmiTenureEnum;
import com.freewayemi.merchant.commons.type.TransactionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IsTenureValid implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsTenureValid.class);

    @Autowired
    public IsTenureValid() {
    }

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        PgTransactionRequest request = (PgTransactionRequest) obj;
        if (Objects.isNull(request.getCardInfo())) {
            LOGGER.error("Card information doesn't exist for seamless transaction params: {}", request);
            throw new FreewayCustomException(TransactionCode.FAILED_157);
        }
        if (Objects.isNull(EmiTenureEnum.getEmiTenureEnum(request.getTenure()))) {
            LOGGER.error("Tenure : {} does not exist for pg transaction request", request.getCardInfo().getCode());
            throw new FreewayCustomException(TransactionCode.FAILED_160);
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
