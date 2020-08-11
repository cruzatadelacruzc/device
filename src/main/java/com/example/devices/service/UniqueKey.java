package com.example.devices.service;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {UniqueKeyValidator.class})
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface UniqueKey {

    Class<?> entityClass();

    String columnName();

    String message() default "uniqueKey.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
