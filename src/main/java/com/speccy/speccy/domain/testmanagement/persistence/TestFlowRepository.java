package com.speccy.speccy.domain.testmanagement.persistence;

import com.speccy.speccy.domain.testmanagement.model.TestFlow;

import java.util.List;
import java.util.Optional;

public interface TestFlowRepository {
    void save(TestFlow testFlow);

    Optional<TestFlow> findById(Long id);

    List<TestFlow> findByPhaseId(Long phaseId);

    void deleteById(Long id);
}
