package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestCaseStatus implements CustomEnumValue<String> {
    ACCEPTED("accepted"),
    DISABLED("disabled"),
    DEPRECATED("deprecated");

    private final String value;

    TestCaseStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
