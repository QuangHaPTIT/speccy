package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestCaseMatchMode;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestCaseMatchModeConverter extends AbstractEnumConverter<TestCaseMatchMode, String> {

    public TestCaseMatchModeConverter() {
        super(TestCaseMatchMode.class);
    }
}
