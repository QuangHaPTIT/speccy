package com.speccy.speccy.application.mapper;

import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.domain.project.model.Phase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhaseMapper {

    @Mapping(target = "status", expression = "java(phase.getStatus() != null ? phase.getStatus().name() : null)")
    PhaseResponse toPhaseResponse(Phase phase);
}
