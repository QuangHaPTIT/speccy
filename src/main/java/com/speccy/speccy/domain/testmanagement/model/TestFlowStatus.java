package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestFlowStatus implements CustomEnumValue<String> {
    DRAFT("draft"),
    ACTIVE("active"),
    ARCHIVED("archived");

    private final String value;

    TestFlowStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
