package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.testmanagement.model.TestCategory;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TestCaseCategoryConverter extends AbstractEnumConverter<TestCategory, String> {

    public TestCaseCategoryConverter() {
        super(TestCategory.class);
    }
}
