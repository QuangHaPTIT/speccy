package com.speccy.speccy.domain.execution.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestRunType implements CustomEnumValue<String> {
    MODULE("module"),
    FLOW("flow"),
    PHASE_ALL("phase_all"),
    MANUAL_SELECT("manual_select");

    private final String value;

    TestRunType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
