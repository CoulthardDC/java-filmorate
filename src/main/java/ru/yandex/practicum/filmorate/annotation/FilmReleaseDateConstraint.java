package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface FilmReleaseDateConstraint {
    String message() default "mast be after 28.12.1895";

    Class <?>[] groups() default  {};

    Class <? extends Payload>[] payload() default {};
}
