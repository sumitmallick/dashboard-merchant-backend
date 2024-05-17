package com.freewayemi.merchant.commons.dto.ntbservices.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AmountValidator.class)
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE,ElementType.FIELD})
public @interface Amount {
    String message() default "Entered Amount Invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
