package com.speccy.speccy.domain.environment.persistence;

import com.speccy.speccy.domain.environment.model.TestDataProfile;

import java.util.List;
import java.util.Optional;

public interface TestDataProfileRepository {
    void save(TestDataProfile profile);

    Optional<TestDataProfile> findById(Long id);

    List<TestDataProfile> findByProjectId(Long projectId);

    Optional<TestDataProfile> findDefaultByProjectId(Long projectId);

    void unsetDefaultByProjectId(Long projectId);

    void deleteById(Long id);
}
