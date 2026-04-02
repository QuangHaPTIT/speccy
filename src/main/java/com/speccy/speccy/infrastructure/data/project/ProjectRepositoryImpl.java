package com.speccy.speccy.infrastructure.data.project;

import com.speccy.speccy.domain.project.model.Project;
import com.speccy.speccy.domain.project.persistence.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final JpaProjectRepository jpaProjectRepository;

    @Override
    public void save(Project project) {
        jpaProjectRepository.save(project);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpaProjectRepository.findById(id);
    }

    @Override
    public List<Project> findByOwnerId(Long ownerId) {
        return jpaProjectRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Project> findAccessibleByUserId(Long userId) {
        return jpaProjectRepository.findAccessibleByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        jpaProjectRepository.deleteById(id);
    }
}
