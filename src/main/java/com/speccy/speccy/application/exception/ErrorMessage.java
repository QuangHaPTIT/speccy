package com.speccy.speccy.application.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@JsonSerialize(using = ErrorMessage.ErrorMessageSerializer.class)
public class ErrorMessage {

    private String message;
    private List<UserError> userErrors;

    private ErrorMessage() {}

    public static ErrorMessageBuilder builder() {
        return new ErrorMessageBuilder();
    }

    public static class ErrorMessageBuilder {
        private String message;
        private List<UserError> userErrors;

        public ErrorMessage build() {
            var error = new ErrorMessage();
            error.message = this.message;
            error.userErrors = this.userErrors;
            return error;
        }

        public ErrorMessageBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorMessageBuilder addFieldError(String field, String message) {
            return addError(
                    UserError.builder()
                            .code(ErrorCode.INVALID_REQUEST.getCode())
                            .message(message)
                            .fields(List.of(field))
                            .build());
        }

        public ErrorMessageBuilder addFieldError(
                ErrorCode errorCode, String field, String message) {
            return addError(
                    UserError.builder()
                            .code(errorCode.getCode())
                            .message(message)
                            .fields(List.of(field))
                            .build());
        }

        public ErrorMessageBuilder addError(UserError userError) {
            if (userErrors == null) {
                userErrors = new ArrayList<>();
            }
            userErrors.add(userError);
            return this;
        }
    }

    public static final class ErrorMessageSerializer extends JsonSerializer<ErrorMessage> {

        @Override
        public void serialize(ErrorMessage value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeStartObject();

            if (value.message != null) {
                gen.writeStringField("error", value.message);
                gen.writeEndObject();
                return;
            }

            if (value.userErrors != null && !value.userErrors.isEmpty()) {
                gen.writeArrayFieldStart("errors");
                for (var userError : value.userErrors) {
                    gen.writeStartObject();
                    gen.writeStringField("code", userError.getCode());
                    gen.writeStringField("message", userError.getMessage());
                    if (userError.getFields() != null && !userError.getFields().isEmpty()) {
                        gen.writeArrayFieldStart("fields");
                        for (var field : userError.getFields()) {
                            gen.writeString(field);
                        }
                        gen.writeEndArray();
                    }
                    gen.writeEndObject();
                }
                gen.writeEndArray();
                gen.writeEndObject();
                return;
            }

            // Fallback when no message or errors are provided
            gen.writeStringField("error", "no message");
            gen.writeEndObject();
        }
    }
}
