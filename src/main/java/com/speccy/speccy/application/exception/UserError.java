package com.speccy.speccy.application.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserError {
    private String code;
    private String message;
    private List<String> fields;
    @JsonIgnore private Object[] messageArgs;

    public static UserError of(ErrorCode errorCode, String message) {
        return UserError.builder().code(errorCode.getCode()).message(message).build();
    }

    public static UserError of(ErrorCode errorCode, String message, List<String> fields) {
        return UserError.builder()
                .code(errorCode.getCode())
                .message(message)
                .fields(fields)
                .build();
    }

    public static UserError of(
            ErrorCode errorCode, String message, List<String> fields, Object[] messageArgs) {
        return UserError.builder()
                .code(errorCode.getCode())
                .message(message)
                .fields(fields)
                .messageArgs(messageArgs)
                .build();
    }
}
