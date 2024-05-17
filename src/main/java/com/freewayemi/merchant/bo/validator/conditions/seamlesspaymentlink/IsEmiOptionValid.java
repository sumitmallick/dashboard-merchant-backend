package com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink;

import com.freewayemi.merchant.bo.SchemeConfigBO;
import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class IsEmiOptionValid implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsEmiOptionValid.class);

    private SchemeConfigBO schemeConfigBO;

    @Autowired
    public IsEmiOptionValid(SchemeConfigBO schemeConfigBO) {
        this.schemeConfigBO = schemeConfigBO;
    }

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        PgTransactionRequest request = (PgTransactionRequest) obj;
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = schemeConfigBO.constructProviderMasterConfigMap(request.getPartner());
        if (Objects.isNull(Util.findEmiOption(request.getCardInfo().getType(), request.getCardInfo().getCode(), request.getTenure(), providerMasterConfigInfoMap))) {
            LOGGER.error("Card interest not found for  Card type: {} , Bank code: {} ,Tenure: {} for pg transaction request : {}", request.getCardInfo().getType(), request.getCardInfo().getCode(), request.getTenure(), request.getCardInfo().getCode());
            throw new FreewayCustomException(TransactionCode.FAILED_162);
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
