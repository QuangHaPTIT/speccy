package com.speccy.speccy.application.service.project.impl;

import com.speccy.speccy.application.exception.ConstraintViolationException;
import com.speccy.speccy.application.exception.ErrorCode;
import com.speccy.speccy.application.mapper.PhaseMapper;
import com.speccy.speccy.application.mapper.ProjectMapper;
import com.speccy.speccy.application.model.project.request.CreatePhaseRequest;
import com.speccy.speccy.application.model.project.request.CreateProjectRequest;
import com.speccy.speccy.application.model.project.request.UpdatePhaseRequest;
import com.speccy.speccy.application.model.project.request.UpdateProjectRequest;
import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.application.model.project.response.ProjectResponse;
import com.speccy.speccy.application.utils.SecurityUtils;
import com.speccy.speccy.domain.identity.model.ProjectRole;
import com.speccy.speccy.domain.project.model.Phase;
import com.speccy.speccy.domain.project.model.Project;
import com.speccy.speccy.domain.project.model.ProjectMember;
import com.speccy.speccy.domain.project.model.ProjectStatus;
import com.speccy.speccy.domain.project.persistence.PhaseRepository;
import com.speccy.speccy.domain.project.persistence.ProjectMemberRepository;
import com.speccy.speccy.domain.project.persistence.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectWriteService implements com.speccy.speccy.application.service.project.ProjectWriteService {

    private static final Set<ProjectRole> MANAGE_PROJECT_ROLES =
            EnumSet.of(ProjectRole.OWNER, ProjectRole.ADMIN);

    private static final Set<ProjectRole> EDIT_PHASE_ROLES =
            EnumSet.of(ProjectRole.OWNER, ProjectRole.ADMIN, ProjectRole.DEVELOPER);

    private final ProjectRepository projectRepository;
    private final PhaseRepository phaseRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectMapper projectMapper;
    private final PhaseMapper phaseMapper;

    @Override
    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        String name = request.getName().strip();
        String description = normalizeNullable(request.getDescription());

        Project project = new Project(currentUserId, name, description);
        projectRepository.save(project);

        projectMemberRepository.save(new ProjectMember(project.getId(), currentUserId, ProjectRole.OWNER, currentUserId));

        return projectMapper.toProjectResponse(project, ProjectRole.OWNER.name());
    }

    @Override
    @Transactional
    public ProjectResponse update(Long projectId, UpdateProjectRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectAccessService.getProjectOrThrow(projectId);
        ProjectRole currentRole = projectAccessService.resolveRole(project, currentUserId);

        requireAnyRole(currentRole, MANAGE_PROJECT_ROLES);
        ensureProjectNotArchived(project);

        project.updateInfo(request.getName().strip(), normalizeNullable(request.getDescription()));

        projectRepository.save(project);

        return projectMapper.toProjectResponse(project, currentRole.name());
    }

    @Override
    @Transactional
    public ProjectResponse archive(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectAccessService.getProjectOrThrow(projectId);
        ProjectRole currentRole = projectAccessService.resolveRole(project, currentUserId);

        requireAnyRole(currentRole, MANAGE_PROJECT_ROLES);

        if (project.getStatus() == ProjectStatus.DELETED) {
            throw new ConstraintViolationException(
                    ErrorCode.INVALID_REQUEST, "Project is deleted", List.of("projectId"));
        }

        project.archive();
        projectRepository.save(project);

        return projectMapper.toProjectResponse(project, currentRole.name());
    }

    @Override
    @Transactional
    public PhaseResponse createPhase(Long projectId, CreatePhaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectAccessService.getProjectOrThrow(projectId);
        ProjectRole currentRole = projectAccessService.resolveRole(project, currentUserId);

        requireAnyRole(currentRole, EDIT_PHASE_ROLES);
        ensureProjectNotArchived(project);

        int nextOrderIndex =
                phaseRepository
                        .findTopByProjectIdOrderByOrderIndexDesc(projectId)
                        .map(phase -> phase.getOrderIndex() + 1)
                        .orElse(1);

        String accumulatedRules = normalizeNullable(request.getAccumulatedRules());
        if (accumulatedRules == null) {
            accumulatedRules = phaseRepository
                            .findTopByProjectIdOrderByOrderIndexDesc(projectId)
                            .map(Phase::getAccumulatedRules)
                            .orElse(null);
        }

        Phase phase = new Phase(
                        projectId,
                        currentUserId,
                        request.getName().strip(),
                        normalizeNullable(request.getDescription()),
                        request.getStartDate(),
                        request.getEndDate(),
                        nextOrderIndex,
                        accumulatedRules);

        phaseRepository.save(phase);

        return phaseMapper.toPhaseResponse(phase);
    }

    @Override
    @Transactional
    public PhaseResponse updatePhase(Long projectId, Long phaseId, UpdatePhaseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectAccessService.getProjectOrThrow(projectId);
        ProjectRole currentRole = projectAccessService.resolveRole(project, currentUserId);

        requireAnyRole(currentRole, EDIT_PHASE_ROLES);
        ensureProjectNotArchived(project);

        Phase phase =
                phaseRepository.findByIdAndProjectId(phaseId, projectId)
                        .orElseThrow(() -> new ConstraintViolationException(ErrorCode.INVALID_REQUEST, "Phase not found", List.of("phaseId")));

        if (!phase.isEditable()) {
            throw new ConstraintViolationException(ErrorCode.INVALID_REQUEST, "Phase is not editable", List.of("phaseId"));
        }

        phase.updateInfo(request.getName().strip(), normalizeNullable(request.getDescription()),
                request.getStartDate(),
                request.getEndDate());

        if (request.getAccumulatedRules() != null) {
            phase.updateAccumulatedRules(normalizeNullable(request.getAccumulatedRules()));
        }

        phaseRepository.save(phase);

        return phaseMapper.toPhaseResponse(phase);
    }

    private void requireAnyRole(ProjectRole currentRole, Set<ProjectRole> allowedRoles) {
        if (!allowedRoles.contains(currentRole)) {
            throw new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
    }

    private void ensureProjectNotArchived(Project project) {
        if (project.getStatus() == ProjectStatus.ARCHIVED || project.getStatus() == ProjectStatus.DELETED) {
            throw new ConstraintViolationException(ErrorCode.INVALID_REQUEST, "Project is archived", List.of("projectId"));
        }
    }

    private String normalizeNullable(String rawValue) {
        if (rawValue == null) {
            return null;
        }

        String normalized = rawValue.strip();
        return normalized.isEmpty() ? null : normalized;
    }
}
