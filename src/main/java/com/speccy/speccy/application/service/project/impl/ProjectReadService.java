package com.speccy.speccy.application.service.project.impl;

import com.speccy.speccy.application.mapper.PhaseMapper;
import com.speccy.speccy.application.mapper.ProjectMapper;
import com.speccy.speccy.application.model.project.response.PhaseResponse;
import com.speccy.speccy.application.model.project.response.ProjectResponse;
import com.speccy.speccy.application.utils.SecurityUtils;
import com.speccy.speccy.domain.identity.model.ProjectRole;
import com.speccy.speccy.domain.project.model.ProjectMember;
import com.speccy.speccy.domain.project.persistence.PhaseRepository;
import com.speccy.speccy.domain.project.persistence.ProjectMemberRepository;
import com.speccy.speccy.domain.project.persistence.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectReadService implements com.speccy.speccy.application.service.project.ProjectReadService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PhaseRepository phaseRepository;
    private final ProjectAccessService projectAccessService;
    private final ProjectMapper projectMapper;
    private final PhaseMapper phaseMapper;

    @Override
    public List<ProjectResponse> listMyProjects() {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Map<Long, ProjectRole> membershipRoleByProjectId =
                projectMemberRepository.findByUserId(currentUserId).stream()
                        .collect(
                                Collectors.toMap(
                                        ProjectMember::getProjectId,
                                        ProjectMember::getRole,
                                        (left, right) -> left));

        var projects = projectRepository.findAccessibleByUserId(currentUserId).stream()
                .map(
                        project -> {
                            ProjectRole currentRole =
                                    project.isOwner(currentUserId)
                                            ? ProjectRole.OWNER
                                            : membershipRoleByProjectId.getOrDefault(
                                                    project.getId(), ProjectRole.VIEWER);

                            return projectMapper.toProjectResponse(project, currentRole.name());
                        })
                .toList();

        return projects;
    }

    @Override
    public ProjectResponse detail(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        var project = projectAccessService.getProjectOrThrow(projectId);
        ProjectRole role = projectAccessService.resolveRole(project, currentUserId);

        return projectMapper.toProjectResponse(project, role.name());
    }

    @Override
    public List<PhaseResponse> listPhases(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        var project = projectAccessService.getProjectOrThrow(projectId);
        projectAccessService.resolveRole(project, currentUserId);

        return phaseRepository.findByProjectIdOrderByOrderIndex(projectId).stream()
                .map(phaseMapper::toPhaseResponse)
                .toList();
    }
}
