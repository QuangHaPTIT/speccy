package com.speccy.speccy.domain.identity.persistence;

import java.util.List;
import java.util.Optional;

import com.speccy.speccy.domain.identity.model.ProjectMember;

public interface ProjectMemberRepository {
    void save(ProjectMember projectMember);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectId(Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
