package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestCaseAuthType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestCaseAuthTypeConverter extends AbstractEnumConverter<TestCaseAuthType, String> {

    public TestCaseAuthTypeConverter() {
        super(TestCaseAuthType.class);
    }
}
