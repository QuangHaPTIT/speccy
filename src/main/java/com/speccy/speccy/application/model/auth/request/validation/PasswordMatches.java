package com.speccy.speccy.application.model.auth.request.validation;

import com.speccy.speccy.application.model.auth.request.UserRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@Documented
@Constraint(validatedBy = PasswordMatches.Validator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Password and confirmPassword do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<PasswordMatches, UserRequest> {

        @Override
        public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            boolean valid = Objects.equals(value.getPassword(), value.getConfirmPassword());
            if (valid) {
                return true;
            }

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            return false;
        }
    }
}
