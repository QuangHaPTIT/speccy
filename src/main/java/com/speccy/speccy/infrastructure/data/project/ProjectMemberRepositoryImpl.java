package com.speccy.speccy.infrastructure.data.project;

import com.speccy.speccy.domain.project.model.ProjectMember;
import com.speccy.speccy.domain.project.persistence.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectMemberRepositoryImpl implements ProjectMemberRepository {

    private final JpaProjectMemberRepository jpaProjectMemberRepository;

    @Override
    public void save(ProjectMember projectMember) {
        jpaProjectMemberRepository.save(projectMember);
    }

    @Override
    public Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId) {
        return jpaProjectMemberRepository.findByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public List<ProjectMember> findByProjectId(Long projectId) {
        return jpaProjectMemberRepository.findByProjectId(projectId);
    }

    @Override
    public List<ProjectMember> findByUserId(Long userId) {
        return jpaProjectMemberRepository.findByUserId(userId);
    }

    @Override
    public void deleteByProjectIdAndUserId(Long projectId, Long userId) {
        jpaProjectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
}
