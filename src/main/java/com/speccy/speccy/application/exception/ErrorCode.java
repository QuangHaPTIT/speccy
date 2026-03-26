package com.speccy.speccy.application.exception;

import lombok.Getter;

import java.util.List;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("ER-001"),
    INVALID_REQUEST("ER-002"),
    INVALID_FORMAT("ER-0003"),
    REQUIRED("ER-0004"),

    ;
    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public UserError toUserError(String message) {
        return UserError.builder().code(this.code).message(message).build();
    }

    public UserError toUserError(String message, List<String> fields) {
        return UserError.builder().code(this.code).message(message).fields(fields).build();
    }
}
