package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.domain.shared.AuditableAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "test_cases")
public class TestCase extends AuditableAggregateRoot<TestCase> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "api_module_id", nullable = false)
    private Long apiModuleId;

    @NotNull
    @Column(name = "phase_id", nullable = false)
    private Long phaseId;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private TestCaseSource source = TestCaseSource.MANUAL;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TestCaseStatus status = TestCaseStatus.DRAFT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "test_category", nullable = false, length = 30)
    private TestCategory testCategory = TestCategory.HAPPY_PATH;

    @Column(name = "replaced_by_id")
    private Long replacedById;

    @NotBlank
    @Size(max = 500)
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @NotBlank
    @Size(max = 10)
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @NotBlank
    @Size(max = 500)
    @Column(name = "endpoint", nullable = false, length = 500)
    private String endpoint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 20)
    private TestCaseAuthType authType = TestCaseAuthType.NONE;

    @Column(name = "request_params_json", columnDefinition = "LONGTEXT")
    private String requestParamsJson;

    @Column(name = "path_variables_json", columnDefinition = "LONGTEXT")
    private String pathVariablesJson;

    @Column(name = "headers_json", columnDefinition = "LONGTEXT")
    private String headersJson;

    @Column(name = "request_body_json", columnDefinition = "LONGTEXT")
    private String requestBodyJson;

    @Column(name = "file_attachments_json", columnDefinition = "LONGTEXT")
    private String fileAttachmentsJson;

    @Column(name = "db_seed_query", columnDefinition = "LONGTEXT")
    private String dbSeedQuery;

    @Column(name = "db_seed_params_json", columnDefinition = "LONGTEXT")
    private String dbSeedParamsJson;

    @Column(name = "db_teardown_query", columnDefinition = "LONGTEXT")
    private String dbTeardownQuery;

    @Column(name = "db_teardown_params_json", columnDefinition = "LONGTEXT")
    private String dbTeardownParamsJson;

    @Column(name = "expected_status")
    private Integer expectedStatus;

    @Column(name = "expected_response_json", columnDefinition = "LONGTEXT")
    private String expectedResponseJson;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "match_mode", nullable = false, length = 20)
    private TestCaseMatchMode matchMode = TestCaseMatchMode.PARTIAL;

    @Column(name = "db_verify_query", columnDefinition = "LONGTEXT")
    private String dbVerifyQuery;

    @Column(name = "db_verify_params_json", columnDefinition = "LONGTEXT")
    private String dbVerifyParamsJson;

    @Column(name = "db_verify_expected", columnDefinition = "LONGTEXT")
    private String dbVerifyExpected;

    @Column(name = "gemini_data_hints", columnDefinition = "LONGTEXT")
    private String geminiDataHints;

    @Column(name = "test_rationale", columnDefinition = "TEXT")
    private String testRationale;

    @Column(name = "source_evidence", columnDefinition = "TEXT")
    private String sourceEvidence;

    @Column(name = "failure_implication", columnDefinition = "TEXT")
    private String failureImplication;

    @Column(name = "order_index")
    private Integer orderIndex;

    public TestCase(Long apiModuleId, Long phaseId, Long createdBy, String name, String method, String endpoint) {
        this.apiModuleId = apiModuleId;
        this.phaseId = phaseId;
        this.createdBy = createdBy;
        this.name = name;
        this.method = method;
        this.endpoint = endpoint;
    }

    public void disable() {
        this.status = TestCaseStatus.DISABLED;
    }

    public void accept() {
        this.status = TestCaseStatus.ACCEPTED;
        this.replacedById = null;
    }

    public void deprecateTo(Long replacedById) {
        this.status = TestCaseStatus.DEPRECATED;
        this.replacedById = replacedById;
    }

    public void assignCategory(TestCategory testCategory) {
        this.testCategory = testCategory == null ? TestCategory.HAPPY_PATH : testCategory;
    }

    public void updateRequest(String method, String endpoint, TestCaseAuthType authType, String headersJson, String paramsJson, String pathVariablesJson, String requestBodyJson) {
        this.method = method;
        this.endpoint = endpoint;
        this.authType = authType;
        this.headersJson = headersJson;
        this.requestParamsJson = paramsJson;
        this.pathVariablesJson = pathVariablesJson;
        this.requestBodyJson = requestBodyJson;
    }

    public void updateExpected(Integer expectedStatus, String expectedResponseJson, TestCaseMatchMode matchMode) {
        this.expectedStatus = expectedStatus;
        this.expectedResponseJson = expectedResponseJson;
        this.matchMode = matchMode;
    }

    public void updateRationale(String testRationale, String sourceEvidence, String failureImplication) {
        this.testRationale = testRationale;
        this.sourceEvidence = sourceEvidence;
        this.failureImplication = failureImplication;
    }
}
