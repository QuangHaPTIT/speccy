package com.speccy.speccy.domain.project.persistence;

import com.speccy.speccy.domain.project.model.Phase;

import java.util.List;
import java.util.Optional;

public interface PhaseRepository {
    void save(Phase phase);

    Optional<Phase> findById(Long id);

    List<Phase> findByProjectIdOrderByOrderIndex(Long projectId);

    void deleteById(Long id);
}
