package com.speccy.speccy.domain.execution.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "test_run_id", nullable = false)
    private Long testRunId;

    @NotNull
    @Column(name = "test_case_id", nullable = false)
    private Long testCaseId;

    @Column(name = "step_order")
    private Integer stepOrder;

    @NotNull
    @Column(name = "passed", nullable = false)
    private Boolean passed = false;

    @NotNull
    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "execution_status", nullable = false, length = 20)
    private ExecutionStatus executionStatus = ExecutionStatus.FAILED;

    @Column(name = "actual_status")
    private Integer actualStatus;

    @Column(name = "actual_response_json", columnDefinition = "LONGTEXT")
    private String actualResponseJson;

    @Column(name = "db_verify_actual", columnDefinition = "LONGTEXT")
    private String dbVerifyActual;

    @Column(name = "db_verify_passed")
    private Boolean dbVerifyPassed;

    @Column(name = "extracted_vars_json", columnDefinition = "LONGTEXT")
    private String extractedVarsJson;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;

    @Column(name = "duration_ms")
    private Integer durationMs;

    public TestResult(Long testRunId, Long testCaseId, Integer stepOrder) {
        this.testRunId = testRunId;
        this.testCaseId = testCaseId;
        this.stepOrder = stepOrder;
        this.passed = false;
    }

    public void complete(
            boolean passed,
            ExecutionStatus executionStatus,
            Integer actualStatus,
            String actualResponseJson,
            String dbVerifyActual,
            Boolean dbVerifyPassed,
            String extractedVarsJson,
            String failureReason,
            String errorDetail,
            Integer durationMs
    ) {
        this.passed = passed;
        this.executionStatus = executionStatus;
        this.actualStatus = actualStatus;
        this.actualResponseJson = actualResponseJson;
        this.dbVerifyActual = dbVerifyActual;
        this.dbVerifyPassed = dbVerifyPassed;
        this.extractedVarsJson = extractedVarsJson;
        this.failureReason = failureReason;
        this.errorDetail = errorDetail;
        this.durationMs = durationMs;
    }
}
