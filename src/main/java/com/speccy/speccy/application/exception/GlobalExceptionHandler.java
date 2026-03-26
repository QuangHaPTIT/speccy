package com.speccy.speccy.application.exception;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<@NonNull ErrorMessage> handleConstraintViolation(
            ConstraintViolationException ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        log.error(ex.toString());

        ErrorMessage originalError = ex.getErrorMessage();

        if (originalError.getUserErrors() != null && !originalError.getUserErrors().isEmpty()) {
            ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder();

            for (UserError userError : originalError.getUserErrors()) {
                String translatedMessage =
                        messageSource.getMessage(
                                userError.getCode(),
                                userError.getMessageArgs(),
                                userError.getMessage(),
                                locale);

                builder.addError(
                        UserError.builder()
                                .code(userError.getCode())
                                .message(translatedMessage)
                                .fields(userError.getFields())
                                .build());
            }

            return ResponseEntity.badRequest().body(builder.build());
        }

        return ResponseEntity.badRequest().body(originalError);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<@NonNull ErrorMessage> handleMissingParameter(
            MissingServletRequestParameterException ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        log.error(ex.toString());

        String message =
                messageSource.getMessage(
                        "validation.required",
                        new Object[] {ex.getParameterName()},
                        String.format("Missing required parameter: %s", ex.getParameterName()),
                        locale);

        return ResponseEntity.badRequest()
                .body(
                        ErrorMessage.builder()
                                .addError(
                                        UserError.builder()
                                                .code(ErrorCode.REQUIRED.getCode())
                                                .message(message)
                                                .fields(List.of(ex.getParameterName()))
                                                .build())
                                .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<@NonNull ErrorMessage> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        log.error(ex.toString());

        String paramName = ex.getName();
        String requiredType =
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String message =
                messageSource.getMessage(
                        "validation.type_mismatch",
                        new Object[] {paramName, requiredType},
                        String.format(
                                "Invalid type for parameter '%s', expected: %s",
                                paramName, requiredType),
                        locale);

        return ResponseEntity.badRequest()
                .body(
                        ErrorMessage.builder()
                                .addFieldError(ErrorCode.INVALID_FORMAT, paramName, message)
                                .build());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<@NonNull ErrorMessage> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        String message =
                messageSource.getMessage(
                        "http.method_not_supported",
                        new Object[] {ex.getMethod()},
                        String.format("Request method '%s' not supported", ex.getMethod()),
                        locale);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorMessage.builder().setMessage(message).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorMessage> handleValidationErrors(
            MethodArgumentNotValidException ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        log.error(ex.toString());

        ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder();

        var fieldErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .sorted(Comparator.comparing(FieldError::getField))
                        .toList();

        for (FieldError fieldError : fieldErrors) {
            try {
                final String regex = "E-\\d{4}";
                String errorCode = fieldError.getDefaultMessage();

                if (errorCode != null && errorCode.matches(regex)) {
                    String message = messageSource.getMessage(errorCode, null, errorCode, locale);

                    builder.addError(
                            UserError.builder()
                                    .code(errorCode)
                                    .message(message)
                                    .fields(List.of(fieldError.getField()))
                                    .build());
                } else {
                    jakarta.validation.ConstraintViolation<?> violation =
                            fieldError.unwrap(jakarta.validation.ConstraintViolation.class);

                    String annotationName =
                            violation
                                    .getConstraintDescriptor()
                                    .getAnnotation()
                                    .annotationType()
                                    .getSimpleName();

                    String fallbackErrorCode = String.format("validation.%s", annotationName);

                    String message =
                            messageSource.getMessage(
                                    fallbackErrorCode,
                                    new Object[] {fieldError.getField()},
                                    fieldError.getDefaultMessage(),
                                    locale);

                    builder.addError(
                            UserError.builder()
                                    .code(fallbackErrorCode)
                                    .message(message)
                                    .fields(List.of(fieldError.getField()))
                                    .build());
                }
            } catch (Exception e) {
                builder.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return ResponseEntity.badRequest().body(builder.build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorMessage> handleGenericException(
            Exception ex,
            @RequestHeader(value = "Accept-Language", defaultValue = "ja") Locale locale) {

        log.error("Unhandled exception occurred", ex);

        String message =
                messageSource.getMessage(
                        "error.internal_server_error", null, "Internal server error", locale);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorMessage.builder().setMessage(message).build());
    }
}
