package com.freewayemi.merchant.commons.dto.ntbservices.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date,String> {
    public static final Logger logger = LoggerFactory.getLogger(DateValidator.class);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (StringUtils.isEmpty(value.isEmpty())) {
                return false;
            }
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
            format.parse(value);
        }catch (Exception e){
            logger.debug("Unable to parse date string");
            return false;
        }
        return true;
    }
}
