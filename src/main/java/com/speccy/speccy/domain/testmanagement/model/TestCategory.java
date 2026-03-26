package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestCategory implements CustomEnumValue<String> {
    HAPPY_PATH("happy_path"),
    VALIDATION_ERROR("validation_error"),
    AUTH_ERROR("auth_error"),
    NOT_FOUND("not_found"),
    EDGE_CASE("edge_case");

    private final String value;

    TestCategory(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
