package com.speccy.speccy.domain.execution.persistence;

import com.speccy.speccy.domain.execution.model.TestRun;
import com.speccy.speccy.domain.execution.model.TestRunStatus;

import java.util.List;
import java.util.Optional;

public interface TestRunRepository {
    void save(TestRun testRun);

    Optional<TestRun> findById(Long id);

    List<TestRun> findByPhaseId(Long phaseId);

    List<TestRun> findByStatus(TestRunStatus status);

    List<TestRun> findByPhaseIdOrderByStartedAtDesc(Long phaseId);
}
