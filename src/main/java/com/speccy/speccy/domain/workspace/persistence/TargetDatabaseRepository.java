package com.speccy.speccy.domain.workspace.persistence;

import com.speccy.speccy.domain.workspace.model.TargetDatabase;

import java.util.List;
import java.util.Optional;

public interface TargetDatabaseRepository {
    void save(TargetDatabase targetDatabase);

    Optional<TargetDatabase> findById(Long id);

    Optional<TargetDatabase> findByProjectIdAndName(Long projectId, String name);

    List<TargetDatabase> findByProjectId(Long projectId);

    void deleteById(Long id);
}
