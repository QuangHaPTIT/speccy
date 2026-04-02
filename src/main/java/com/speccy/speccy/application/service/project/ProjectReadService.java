package com.speccy.speccy.application.service.project;

import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.application.model.project.response.ProjectResponse;

import java.util.List;

public interface ProjectReadService {
    List<ProjectResponse> listMyProjects();

    ProjectResponse detail(Long projectId);

    List<PhaseResponse> listPhases(Long projectId);
}
