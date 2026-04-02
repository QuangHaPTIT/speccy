package com.speccy.speccy.application.model.project.request.validation;

import com.speccy.speccy.application.model.project.request.CreatePhaseRequest;
import com.speccy.speccy.application.model.project.request.UpdatePhaseRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {ValidPhaseDateRange.CreateValidator.class, ValidPhaseDateRange.UpdateValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhaseDateRange {

    String message() default "End date must be after or equal to start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CreateValidator implements ConstraintValidator<ValidPhaseDateRange, CreatePhaseRequest> {
        @Override
        public boolean isValid(CreatePhaseRequest value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            if (value.getStartDate() == null || value.getEndDate() == null || !value.getEndDate().isBefore(value.getStartDate())) {
                return true;
            }

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
    }

    class UpdateValidator implements ConstraintValidator<ValidPhaseDateRange, UpdatePhaseRequest> {
        @Override
        public boolean isValid(UpdatePhaseRequest value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            if (value.getStartDate() == null || value.getEndDate() == null || !value.getEndDate().isBefore(value.getStartDate())) {
                return true;
            }

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            return false;
        }
    }
}
