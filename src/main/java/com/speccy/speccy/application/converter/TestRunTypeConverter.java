package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.execution.model.TestRunType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestRunTypeConverter extends AbstractEnumConverter<TestRunType, String> {

    public TestRunTypeConverter() {
        super(TestRunType.class);
    }
}
