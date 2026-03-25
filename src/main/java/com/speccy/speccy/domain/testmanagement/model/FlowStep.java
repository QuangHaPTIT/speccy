package com.speccy.speccy.domain.testmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "flow_steps")
public class FlowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "flow_id", nullable = false)
    private Long flowId;

    @NotNull
    @Column(name = "test_case_id", nullable = false)
    private Long testCaseId;

    @NotNull
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "override_headers_json", columnDefinition = "LONGTEXT")
    private String overrideHeadersJson;

    @Column(name = "override_body_json", columnDefinition = "LONGTEXT")
    private String overrideBodyJson;

    @Column(name = "override_params_json", columnDefinition = "LONGTEXT")
    private String overrideParamsJson;

    @Size(max = 255)
    @Column(name = "extract_field", length = 255)
    private String extractField;

    @Size(max = 255)
    @Column(name = "inject_to_var", length = 255)
    private String injectToVar;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public FlowStep(Long flowId, Long testCaseId, Integer stepOrder) {
        this.flowId = flowId;
        this.testCaseId = testCaseId;
        this.stepOrder = stepOrder;
    }

    public void updateOverrides(String overrideHeadersJson, String overrideBodyJson, String overrideParamsJson) {
        this.overrideHeadersJson = overrideHeadersJson;
        this.overrideBodyJson = overrideBodyJson;
        this.overrideParamsJson = overrideParamsJson;
    }

    public void updateExtraction(String extractField, String injectToVar) {
        this.extractField = extractField;
        this.injectToVar = injectToVar;
    }

    public void updateStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }
}
