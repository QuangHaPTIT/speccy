package com.speccy.speccy.application.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ConstraintViolationException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ConstraintViolationException(String message) {
        super(message);
        this.errorMessage = ErrorMessage.builder().setMessage(message).build();
    }

    public ConstraintViolationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message));
        this.errorMessage = ErrorMessage.builder().addFieldError(field, message).build();
    }

    public ConstraintViolationException(ErrorCode errorCode, String message) {
        super(String.format("[%s] %s", errorCode.getCode(), message));
        this.errorMessage =
                ErrorMessage.builder().addError(UserError.of(errorCode, message)).build();
    }

    public ConstraintViolationException(ErrorCode errorCode, String message, List<String> fields) {
        super(String.format("[%s] %s", errorCode.getCode(), message));
        this.errorMessage =
                ErrorMessage.builder().addError(UserError.of(errorCode, message, fields)).build();
    }

    public ConstraintViolationException(
            ErrorCode errorCode, String message, List<String> fields, Object[] messageArgs) {
        super(String.format("[%s] %s", errorCode.getCode(), message));
        this.errorMessage =
                ErrorMessage.builder()
                        .addError(UserError.of(errorCode, message, fields, messageArgs))
                        .build();
    }

    public ConstraintViolationException(UserError userError) {
        super(String.format("[%s] %s", userError.getCode(), userError.getMessage()));
        this.errorMessage = ErrorMessage.builder().addError(userError).build();
    }

    public ConstraintViolationException(ErrorMessage errorMessage) {
        super("Constraint violation");
        this.errorMessage = errorMessage;
    }
}
