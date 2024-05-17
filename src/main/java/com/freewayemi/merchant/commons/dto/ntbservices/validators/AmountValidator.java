package com.freewayemi.merchant.commons.dto.ntbservices.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmountValidator implements ConstraintValidator<Amount,String> {

    public static final Logger logger = LoggerFactory.getLogger(AmountValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (StringUtils.isEmpty(value.isEmpty())) {
                return false;
            }
            return Double.parseDouble(value) > 0;
        }catch (Exception e){
            logger.debug("Unable to parse amount");
            return false;
        }
    }
}
