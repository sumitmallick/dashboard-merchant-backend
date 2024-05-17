package com.freewayemi.merchant.commons.dto.ntbservices.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateValidator.class)
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE,ElementType.FIELD})
public @interface Date {

    String message() default "Entered Date Invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
