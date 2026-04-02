package com.speccy.speccy.infrastructure.data.project;

import com.speccy.speccy.domain.project.model.Phase;
import com.speccy.speccy.domain.project.persistence.PhaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PhaseRepositoryImpl implements PhaseRepository {

    private final JpaPhaseRepository jpaPhaseRepository;

    @Override
    public void save(Phase phase) {
        jpaPhaseRepository.save(phase);
    }

    @Override
    public Optional<Phase> findById(Long id) {
        return jpaPhaseRepository.findById(id);
    }

    @Override
    public Optional<Phase> findByIdAndProjectId(Long id, Long projectId) {
        return jpaPhaseRepository.findByIdAndProjectId(id, projectId);
    }

    @Override
    public List<Phase> findByProjectIdOrderByOrderIndex(Long projectId) {
        return jpaPhaseRepository.findByProjectIdOrderByOrderIndex(projectId);
    }

    @Override
    public Optional<Phase> findTopByProjectIdOrderByOrderIndexDesc(Long projectId) {
        return jpaPhaseRepository.findTopByProjectIdOrderByOrderIndexDesc(projectId);
    }

    @Override
    public void deleteById(Long id) {
        jpaPhaseRepository.deleteById(id);
    }
}
