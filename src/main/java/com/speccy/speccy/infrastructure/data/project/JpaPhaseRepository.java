package com.speccy.speccy.infrastructure.data.project;

import com.speccy.speccy.domain.project.model.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPhaseRepository extends JpaRepository<Phase, Long> {
    Optional<Phase> findByIdAndProjectId(Long id, Long projectId);

    List<Phase> findByProjectIdOrderByOrderIndex(Long projectId);

    Optional<Phase> findTopByProjectIdOrderByOrderIndexDesc(Long projectId);
}
