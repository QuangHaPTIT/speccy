package com.speccy.speccy.domain.workspace.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TargetDatabaseStatus implements CustomEnumValue<String> {
    ACTIVE("active"),
    UNREACHABLE("unreachable"),
    UNTESTED("untested");

    private final String value;

    TargetDatabaseStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
