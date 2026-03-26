package com.speccy.speccy.domain.project.model;

import com.speccy.speccy.domain.shared.AuditableAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "phases")
public class Phase extends AuditableAggregateRoot<Phase> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PhaseStatus status = PhaseStatus.PLANNING;

    @Column(name = "accumulated_rules", columnDefinition = "LONGTEXT")
    private String accumulatedRules;

    public Phase(Long projectId, Long createdBy, String name, String description,
                 LocalDate startDate, LocalDate endDate,
                 Integer orderIndex, String accumulatedRules) {
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderIndex = orderIndex;
        this.accumulatedRules = accumulatedRules;
        this.status = PhaseStatus.PLANNING;
    }

    public void updateInfo(String name, String description,
                           LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateAccumulatedRules(String accumulatedRules) {
        this.accumulatedRules = accumulatedRules;
    }

    public void activate() {
        this.status = PhaseStatus.ACTIVE;
    }

    public void complete() {
        this.status = PhaseStatus.COMPLETED;
    }

    public void cancel() {
        this.status = PhaseStatus.CANCELLED;
    }

    public void reorder(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isEditable() {
        return this.status == PhaseStatus.PLANNING
                || this.status == PhaseStatus.ACTIVE;
    }
}
