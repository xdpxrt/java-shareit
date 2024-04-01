package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = ru.practicum.shareit.booking.validator.DateRangeValidator.class)
public @interface DateRange {
    String start();

    String end();

    String message() default "Некорректно указаны даты";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}