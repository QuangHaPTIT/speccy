package com.speccy.speccy.domain.user.persistence;

import com.speccy.speccy.domain.user.model.ProjectMember;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository {
    void save(ProjectMember projectMember);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectId(Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
