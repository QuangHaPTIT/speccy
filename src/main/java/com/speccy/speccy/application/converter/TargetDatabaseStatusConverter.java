package com.speccy.speccy.application.converter;

import com.speccy.speccy.domain.workspace.model.TargetDatabaseStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TargetDatabaseStatusConverter extends AbstractEnumConverter<TargetDatabaseStatus, String> {

    public TargetDatabaseStatusConverter() {
        super(TargetDatabaseStatus.class);
    }
}
