package com.speccy.speccy.interfaces.rest;

import com.speccy.speccy.application.model.project.request.CreatePhaseRequest;
import com.speccy.speccy.application.model.project.request.CreateProjectRequest;
import com.speccy.speccy.application.model.project.request.UpdatePhaseRequest;
import com.speccy.speccy.application.model.project.request.UpdateProjectRequest;
import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.application.model.project.response.ProjectResponse;
import com.speccy.speccy.application.service.project.ProjectReadService;
import com.speccy.speccy.application.service.project.ProjectWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectReadService projectReadService;
    private final ProjectWriteService projectWriteService;

    @GetMapping
    public List<ProjectResponse> list() {
        return projectReadService.listMyProjects();
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectWriteService.create(request);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse detail(@PathVariable Long projectId) {
        return projectReadService.detail(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(@PathVariable Long projectId, @Valid @RequestBody UpdateProjectRequest request) {
        return projectWriteService.update(projectId, request);
    }

    @PostMapping("/{projectId}/archive")
    public ProjectResponse archive(@PathVariable Long projectId) {
        return projectWriteService.archive(projectId);
    }

    @GetMapping("/{projectId}/phases")
    public List<PhaseResponse> listPhases(@PathVariable Long projectId) {
        return projectReadService.listPhases(projectId);
    }

    @PostMapping("/{projectId}/phases")
    public PhaseResponse createPhase(@PathVariable Long projectId, @Valid @RequestBody CreatePhaseRequest request) {
        return projectWriteService.createPhase(projectId, request);
    }

    @PutMapping("/{projectId}/phases/{phaseId}")
    public PhaseResponse updatePhase(@PathVariable Long projectId, @PathVariable Long phaseId, @Valid @RequestBody UpdatePhaseRequest request) {
        return projectWriteService.updatePhase(projectId, phaseId, request);
    }
}
