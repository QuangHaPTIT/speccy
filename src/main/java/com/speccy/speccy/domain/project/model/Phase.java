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
        validateDateRange(startDate, endDate);
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderIndex = orderIndex;
        this.accumulatedRules = accumulatedRules;
    }

    public void updateInfo(String name, String description,
                           LocalDate startDate, LocalDate endDate) {
        ensureEditable("Phase is not editable");
        validateDateRange(startDate, endDate);
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateAccumulatedRules(String accumulatedRules) {
        this.accumulatedRules = accumulatedRules;
    }

    public void activate() {
        if (this.status != PhaseStatus.PLANNING) {
            throw new IllegalStateException("Only planning phase can be activated");
        }
        this.status = PhaseStatus.ACTIVE;
    }

    public void complete() {
        if (this.status != PhaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active phase can be completed");
        }
        this.status = PhaseStatus.COMPLETED;
    }

    public void cancel() {
        ensureEditable("Only planning or active phase can be cancelled");
        this.status = PhaseStatus.CANCELLED;
    }

    public void reorder(Integer orderIndex) {
        ensureEditable("Phase is not editable");
        if (orderIndex == null || orderIndex <= 0) {
            throw new IllegalArgumentException("Order index must be positive");
        }
        this.orderIndex = orderIndex;
    }

    public boolean isEditable() {
        return this.status == PhaseStatus.PLANNING || this.status == PhaseStatus.ACTIVE;
    }

    private void ensureEditable(String message) {
        if (!isEditable()) {
            throw new IllegalStateException(message);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after or equal to start date");
        }
    }
}
