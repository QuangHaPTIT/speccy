package com.speccy.speccy.domain.execution.persistence;

import com.speccy.speccy.domain.execution.model.TestResult;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository {
    void save(TestResult testResult);

    Optional<TestResult> findById(Long id);

    List<TestResult> findByTestRunId(Long testRunId);

    List<TestResult> findByTestRunIdOrderByStepOrder(Long testRunId);

    List<TestResult> findByTestRunIdAndPassed(Long testRunId, Boolean passed);
}
