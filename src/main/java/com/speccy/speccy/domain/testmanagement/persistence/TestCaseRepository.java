package com.speccy.speccy.domain.testmanagement.persistence;

import com.speccy.speccy.domain.testmanagement.model.TestCase;
import com.speccy.speccy.domain.testmanagement.model.TestCaseStatus;

import java.util.List;
import java.util.Optional;

public interface TestCaseRepository {
    void save(TestCase testCase);

    Optional<TestCase> findById(Long id);

    List<TestCase> findByApiModuleIdOrderByOrderIndex(Long apiModuleId);

    List<TestCase> findByPhaseId(Long phaseId);

    List<TestCase> findByStatus(TestCaseStatus status);

    void deleteById(Long id);
}
