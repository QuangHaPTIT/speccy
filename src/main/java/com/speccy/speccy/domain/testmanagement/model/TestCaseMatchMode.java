package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestCaseMatchMode implements CustomEnumValue<String> {
    EXACT("exact"),
    PARTIAL("partial"),
    STATUS_ONLY("status_only");

    private final String value;

    TestCaseMatchMode(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
