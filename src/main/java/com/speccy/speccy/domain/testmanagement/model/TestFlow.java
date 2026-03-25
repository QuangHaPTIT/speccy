package com.speccy.speccy.domain.testmanagement.model;

import com.speccy.speccy.application.converter.TestFlowStatusConverter;
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

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "test_flows")
public class TestFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "phase_id", nullable = false)
    private Long phaseId;

    @NotBlank
    @Size(max = 500)
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @NotNull
    @Convert(converter = TestFlowStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private TestFlowStatus status = TestFlowStatus.DRAFT;

    public TestFlow(Long phaseId, String name) {
        this.phaseId = phaseId;
        this.name = name;
    }

    public void activate() {
        this.status = TestFlowStatus.ACTIVE;
    }

    public void archive() {
        this.status = TestFlowStatus.ARCHIVED;
    }

    public void setDraft() {
        this.status = TestFlowStatus.DRAFT;
    }
}
