package com.speccy.speccy.application.service.project.impl;

import com.speccy.speccy.application.exception.ConstraintViolationException;
import com.speccy.speccy.application.exception.ErrorCode;
import com.speccy.speccy.domain.identity.model.ProjectRole;
import com.speccy.speccy.domain.project.model.Project;
import com.speccy.speccy.domain.project.model.ProjectMember;
import com.speccy.speccy.domain.project.model.ProjectStatus;
import com.speccy.speccy.domain.project.persistence.ProjectMemberRepository;
import com.speccy.speccy.domain.project.persistence.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectAccessService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public Project getProjectOrThrow(Long projectId) {
        return projectRepository
                .findById(projectId)
                .filter(project -> project.getStatus() != ProjectStatus.DELETED)
                .orElseThrow(() -> new ConstraintViolationException(ErrorCode.INVALID_REQUEST, "Project not found",
                                        List.of("projectId")));
    }

    public ProjectRole resolveRole(Project project, Long userId) {
        if (project.isOwner(userId)) {
            return ProjectRole.OWNER;
        }

        ProjectMember member = projectMemberRepository
                            .findByProjectIdAndUserId(project.getId(), userId)
                            .orElseThrow(() -> new ConstraintViolationException(ErrorCode.UNAUTHORIZED, "Unauthorized project access"));
        return member.getRole();
    }
}
