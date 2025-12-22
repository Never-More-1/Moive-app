package movieApp.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import movieApp.service.validation.AgeValidator;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;

@Constraint(validatedBy = {AgeValidator.class})
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface AdultAge {
    String message() default "Возраст должен быть от 18 до 120";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
