package com.speccy.speccy.domain.project.persistence;

import com.speccy.speccy.domain.project.model.ProjectMember;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository {
    void save(ProjectMember projectMember);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectId(Long projectId);

    List<ProjectMember> findByUserId(Long userId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
