package com.speccy.speccy.domain.spec.persistence;

import com.speccy.speccy.domain.spec.model.Spec;

import java.util.List;
import java.util.Optional;

public interface SpecRepository {
    void save(Spec spec);

    Optional<Spec> findById(Long id);

    List<Spec> findByPhaseId(Long phaseId);

    void deleteById(Long id);
}
