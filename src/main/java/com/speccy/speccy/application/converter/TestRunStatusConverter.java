package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.execution.model.TestRunStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestRunStatusConverter extends AbstractEnumConverter<TestRunStatus, String> {

    public TestRunStatusConverter() {
        super(TestRunStatus.class);
    }
}
