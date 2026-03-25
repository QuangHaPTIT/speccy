package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.CustomEnumValue;

public enum TestCaseAuthType implements CustomEnumValue<String> {
    NONE("none"),
    BEARER("bearer"),
    API_KEY("api_key"),
    FLOW_INHERIT("flow_inherit");

    private final String value;

    TestCaseAuthType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
