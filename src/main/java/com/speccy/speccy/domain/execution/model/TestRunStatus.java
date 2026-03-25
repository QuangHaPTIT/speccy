package com.speccy.speccy.domain.execution.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestRunStatus implements CustomEnumValue<String> {
    QUEUED("queued"),
    RUNNING("running"),
    DONE("done"),
    FAILED("failed"),
    TIMEOUT("timeout");

    private final String value;

    TestRunStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
