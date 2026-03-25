package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestCaseSource;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestCaseSourceConverter extends AbstractEnumConverter<TestCaseSource, String> {

    public TestCaseSourceConverter() {
        super(TestCaseSource.class);
    }
}
