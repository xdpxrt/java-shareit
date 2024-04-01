package ru.practicum.shareit.booking.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    String start;
    String end;
    String message;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        start = constraintAnnotation.start();
        end = constraintAnnotation.end();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext cxt) {
        try {
            Field startField = object.getClass().getDeclaredField(start);
            startField.setAccessible(true);
            Field endField = object.getClass().getDeclaredField(end);
            endField.setAccessible(true);
            LocalDateTime start = (LocalDateTime) startField.get(object);
            LocalDateTime end = (LocalDateTime) endField.get(object);
            if (start == null || end == null) {
                return false;
            }
            return end.isAfter(start) && !end.isEqual(start) &&
                    !end.isEqual(LocalDateTime.now()) && start.isAfter(LocalDateTime.now());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}