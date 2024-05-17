package com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink;


import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.TransactionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class IsCardTypeValid implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsCardTypeValid.class);

    @Autowired
    public IsCardTypeValid() {
    }

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        PgTransactionRequest request = (PgTransactionRequest) obj;
        if (Objects.isNull(request.getCardInfo())) {
            LOGGER.error("Card information doesn't exist for seamless transaction params: {}", request);
            throw new FreewayCustomException(TransactionCode.FAILED_157);
        }
        if (Objects.isNull(CardTypeEnum.getCardTypeEnum(request.getCardInfo().getType()))) {
            LOGGER.error("Card type : {} does not exist for pg transaction request", request.getCardInfo().getCode());
            throw new FreewayCustomException(TransactionCode.FAILED_159);
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
