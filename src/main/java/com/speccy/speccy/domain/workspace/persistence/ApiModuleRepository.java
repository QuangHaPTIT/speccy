package com.speccy.speccy.domain.workspace.persistence;

import com.speccy.speccy.domain.workspace.model.ApiModule;

import java.util.List;
import java.util.Optional;

public interface ApiModuleRepository {
    void save(ApiModule apiModule);

    Optional<ApiModule> findById(Long id);

    List<ApiModule> findByProjectIdOrderByOrderIndex(Long projectId);

    void deleteById(Long id);
}
