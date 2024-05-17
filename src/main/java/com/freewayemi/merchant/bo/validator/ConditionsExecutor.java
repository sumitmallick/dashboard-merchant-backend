package com.freewayemi.merchant.bo.validator;

import com.freewayemi.merchant.commons.bo.validators.conditions.GetConditions;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConditionsExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionsExecutor.class);

    private final GetConditions refundPayoutsGetConditions;
    private final GetConditions getDeliveryOrderDetailsGetConditions;

    private final GetConditions seamlessPaymentLinkGetConditions;

    @Autowired
    public ConditionsExecutor(@Qualifier("refundPayoutsGetConditions") GetConditions refundPayoutsGetConditions,
                              @Qualifier("getDeliveryOrderDetailsGetConditions") GetConditions getDeliveryOrderDetailsGetConditions,
                              @Qualifier("seamlessPaymentLinkGetConditions") GetConditions seamlessPaymentLinkGetConditions) {
        this.refundPayoutsGetConditions = refundPayoutsGetConditions;
        this.getDeliveryOrderDetailsGetConditions = getDeliveryOrderDetailsGetConditions;
        this.seamlessPaymentLinkGetConditions = seamlessPaymentLinkGetConditions;
    }

    private void processConditions(List<Conditions> conditionsList, Object obj) {
        for (Conditions conditions : conditionsList) {
            conditions.executeConditions(obj);
        }
    }

    public void executeConditions(ConditionType conditionType, Object obj) throws FreewayException {
        switch (conditionType) {
            case CREATE_REFUND_PAYOUT_CONDITIONS:
                processConditions(refundPayoutsGetConditions.getConditionsList(), obj);
                break;
            case GET_DELIVERY_ORDER_DETAILS:
                processConditions(getDeliveryOrderDetailsGetConditions.getConditionsList(), obj);
                break;
            case SEALMESS_PAYMENTLINK_CONDITIONS:
                processConditions(seamlessPaymentLinkGetConditions.getConditionsList(), obj);
            default:
                LOGGER.error("no condition type available to execute conditions");
                break;
        }
    }
}
