package ru.chuchkalov.taskmanager.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoAdminValidator.class)
public @interface NoAdmin {

    String message() default "Username cannot contain the word 'admin'";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
