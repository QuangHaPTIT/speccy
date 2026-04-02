package com.speccy.speccy.domain.project.persistence;

import com.speccy.speccy.domain.project.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    Optional<Project> findById(Long id);

    List<Project> findByOwnerId(Long ownerId);

    List<Project> findAccessibleByUserId(Long userId);

    void deleteById(Long id);
}
