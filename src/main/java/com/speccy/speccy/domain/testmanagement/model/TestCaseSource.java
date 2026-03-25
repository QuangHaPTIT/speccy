package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestCaseSource implements CustomEnumValue<String> {
    GEMINI("gemini"),
    MANUAL("manual");

    private final String value;

    TestCaseSource(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
