package com.speccy.speccy.domain.execution.model;

import com.speccy.speccy.application.converter.TestRunStatusConverter;
import com.speccy.speccy.application.converter.TestRunTypeConverter;
import com.speccy.speccy.application.utils.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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

import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "test_runs")
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "phase_id", nullable = false)
    private Long phaseId;

    @NotNull
    @Column(name = "triggered_by", nullable = false)
    private Long triggeredBy;

    @NotNull
    @Column(name = "db_connection_id", nullable = false)
    private Long dbConnectionId;

    @Column(name = "test_data_profile_id")
    private Long testDataProfileId;

    @NotNull
    @Convert(converter = TestRunTypeConverter.class)
    @Column(name = "run_type", nullable = false, length = 20)
    private TestRunType runType;

    @Column(name = "ref_id")
    private Long refId;

    @NotBlank
    @Size(max = 500)
    @Column(name = "target_url", nullable = false, length = 500)
    private String targetUrl;

    @Column(name = "profile_snapshot_json", columnDefinition = "LONGTEXT")
    private String profileSnapshotJson;

    @Size(max = 128)
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @NotNull
    @Convert(converter = TestRunStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private TestRunStatus status = TestRunStatus.QUEUED;

    @NotNull
    @Column(name = "total", nullable = false)
    private Integer total = 0;

    @NotNull
    @Column(name = "passed", nullable = false)
    private Integer passed = 0;

    @NotNull
    @Column(name = "failed", nullable = false)
    private Integer failed = 0;

    @NotNull
    @Column(name = "skipped", nullable = false)
    private Integer skipped = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public TestRun(
            Long phaseId,
            Long triggeredBy,
            Long dbConnectionId,
            Long testDataProfileId,
            TestRunType runType,
            Long refId,
            String targetUrl,
            String profileSnapshotJson,
            String idempotencyKey
    ) {
        this.phaseId = phaseId;
        this.triggeredBy = triggeredBy;
        this.dbConnectionId = dbConnectionId;
        this.testDataProfileId = testDataProfileId;
        this.runType = runType;
        this.refId = refId;
        this.targetUrl = targetUrl;
        this.profileSnapshotJson = profileSnapshotJson;
        this.idempotencyKey = idempotencyKey;
        this.total = 0;
        this.passed = 0;
        this.failed = 0;
        this.skipped = 0;
    }

    public void markRunning() {
        this.status = TestRunStatus.RUNNING;
        this.startedAt = Util.getNowUTC();
    }

    public void markDone(int total, int passed, int failed, int skipped) {
        this.status = TestRunStatus.DONE;
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.finishedAt = Util.getNowUTC();
    }

    public void markFailed(int total, int passed, int failed, int skipped) {
        this.status = TestRunStatus.FAILED;
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.finishedAt = Util.getNowUTC();
    }

    public void markTimeout(int total, int passed, int failed, int skipped) {
        this.status = TestRunStatus.TIMEOUT;
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.finishedAt = Util.getNowUTC();
    }
}
