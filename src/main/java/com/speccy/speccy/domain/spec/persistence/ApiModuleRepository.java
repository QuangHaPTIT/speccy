package com.speccy.speccy.domain.spec.persistence;

import com.speccy.speccy.domain.spec.model.ApiModule;

import java.util.List;
import java.util.Optional;

public interface ApiModuleRepository {
    void save(ApiModule apiModule);

    Optional<ApiModule> findById(Long id);

    List<ApiModule> findBySpecIdOrderByOrderIndex(Long specId);

    List<ApiModule> findByProjectIdOrderByOrderIndex(Long projectId);

    void deleteById(Long id);
}
