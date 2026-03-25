package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestCaseStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestCaseStatusConverter extends AbstractEnumConverter<TestCaseStatus, String> {

    public TestCaseStatusConverter() {
        super(TestCaseStatus.class);
    }
}
