package ru.practicum.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<CorrectDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime ld, ConstraintValidatorContext constraintValidatorContext) {
        return !ld.isBefore(LocalDateTime.now().minusHours(2));
    }
}