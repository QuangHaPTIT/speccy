package com.speccy.speccy.domain.testmanagement.persistence;

import com.speccy.speccy.domain.testmanagement.model.FlowStep;

import java.util.List;
import java.util.Optional;

public interface FlowStepRepository {
    void save(FlowStep flowStep);

    Optional<FlowStep> findById(Long id);

    List<FlowStep> findByFlowIdOrderByStepOrder(Long flowId);

    void deleteById(Long id);

    void deleteByFlowId(Long flowId);
}
