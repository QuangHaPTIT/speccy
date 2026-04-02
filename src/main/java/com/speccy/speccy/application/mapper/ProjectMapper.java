package com.speccy.speccy.application.mapper;

import com.speccy.speccy.application.model.project.response.ProjectResponse;
import com.speccy.speccy.domain.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "status", expression = "java(project.getStatus() != null ? project.getStatus().name() : null)")
    @Mapping(target = "currentUserRole", source = "currentUserRole")
    ProjectResponse toProjectResponse(Project project, String currentUserRole);
}
