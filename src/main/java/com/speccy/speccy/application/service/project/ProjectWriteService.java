package com.speccy.speccy.application.service.project;

import com.speccy.speccy.application.model.project.request.CreatePhaseRequest;
import com.speccy.speccy.application.model.project.request.CreateProjectRequest;
import com.speccy.speccy.application.model.project.request.UpdatePhaseRequest;
import com.speccy.speccy.application.model.project.request.UpdateProjectRequest;
import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.application.model.project.response.ProjectResponse;

public interface ProjectWriteService {
    ProjectResponse create(CreateProjectRequest request);

    ProjectResponse update(Long projectId, UpdateProjectRequest request);

    ProjectResponse archive(Long projectId);

    PhaseResponse createPhase(Long projectId, CreatePhaseRequest request);

    PhaseResponse updatePhase(Long projectId, Long phaseId, UpdatePhaseRequest request);
}
