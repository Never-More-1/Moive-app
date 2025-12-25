package movieApp.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import movieApp.annotation.AdultAge;

public class AgeValidator implements ConstraintValidator<AdultAge, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && value >= 1 && value <= 120;
    }
}
