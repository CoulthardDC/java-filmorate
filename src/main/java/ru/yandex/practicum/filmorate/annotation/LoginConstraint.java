package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.LoginValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginValidator.class)
public @interface LoginConstraint {
    String message() default "must not contain spaces and must not be empty";

    Class <?>[] groups() default  {};

    Class <? extends Payload>[] payload() default {};
}
