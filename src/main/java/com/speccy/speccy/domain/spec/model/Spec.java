package com.speccy.speccy.domain.spec.model;

import com.speccy.speccy.domain.shared.AuditableAggregateRoot;
import jakarta.persistence.*;
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
@Table(name = "specs")
public class Spec extends AuditableAggregateRoot<Spec> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "phase_id", nullable = false)
    private Long phaseId;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @NotBlank
    @Size(max = 500)
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    // Nội dung gốc dev paste vào — lưu để có thể re-gen khi cần.
    // Đây là free text, không có cấu trúc cố định.
    @Column(name = "raw_content", columnDefinition = "LONGTEXT")
    private String rawContent;

    // Structured spec JSON dùng làm input chính cho Gemini.
    @Column(name = "structured_spec_json", columnDefinition = "LONGTEXT")
    private String structuredSpecJson;

    // OpenAPI content do hệ thống tạo/lưu trữ sau parse.
    @Column(name = "openapi_content", columnDefinition = "LONGTEXT")
    private String openapiContent;

    // Định dạng openapi_content: JSON hoặc YAML.
    @Column(name = "openapi_format", length = 10)
    private String openapiFormat;

    // Snapshot context từ các phase trước (để audit/debug khi gen test)
    @Column(name = "phase_context_snapshot", columnDefinition = "LONGTEXT")
    private String phaseContextSnapshot;

    // Kết quả phân tích ảnh hưởng của Gemini theo endpoint
    @Column(name = "impact_analysis_json", columnDefinition = "LONGTEXT")
    private String impactAnalysisJson;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status", nullable = false, length = 20)
    private SpecParseStatus parseStatus = SpecParseStatus.PENDING;

    @Column(name = "parse_error", columnDefinition = "TEXT")
    private String parseError;

    // Số token Gemini đã dùng cho lần parse này — tracking chi phí.
    @Column(name = "gemini_tokens_used")
    private Integer geminiTokensUsed;

    public Spec(Long phaseId, Long createdBy, String title, String rawContent) {
        this.phaseId = phaseId;
        this.createdBy = createdBy;
        this.title = title;
        this.rawContent = rawContent;
    }

    public void updateContent(String title, String rawContent) {
        this.title = title;
        this.rawContent = rawContent;
        // Reset về PENDING để trigger re-gen nếu cần
        this.parseStatus = SpecParseStatus.PENDING;
        this.parseError = null;
    }

    public void markProcessing() {
        this.parseStatus = SpecParseStatus.PROCESSING;
    }

    public void updateStructuredSpec(String structuredSpecJson) {
        this.structuredSpecJson = structuredSpecJson;
    }

    public void markDone(String openapiContent, String openapiFormat, Integer geminiTokensUsed) {
        this.openapiContent = openapiContent;
        this.openapiFormat = openapiFormat;
        this.geminiTokensUsed = geminiTokensUsed;
        this.parseStatus = SpecParseStatus.DONE;
        this.parseError = null;
    }

    public void markFailed(String parseError) {
        this.parseStatus = SpecParseStatus.FAILED;
        this.parseError = parseError;
    }

    public boolean isReadyForTestGen() {
        return this.parseStatus == SpecParseStatus.DONE
                && this.openapiContent != null
                && this.openapiFormat != null;
    }
}
