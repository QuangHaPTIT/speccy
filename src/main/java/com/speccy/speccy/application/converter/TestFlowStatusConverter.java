package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestFlowStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestFlowStatusConverter extends AbstractEnumConverter<TestFlowStatus, String> {

    public TestFlowStatusConverter() {
        super(TestFlowStatus.class);
    }
}
