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

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "projects")
public class Project extends AuditableAggregateRoot<Project> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    public Project(Long ownerId, String name, String description) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
    }

    public void updateInfo(String name, String description) {
        assertMutable();
        this.name = name;
        this.description = description;
    }

    public void archive() {
        if (this.status == ProjectStatus.DELETED) {
            throw new IllegalStateException("Deleted project cannot be archived");
        }
        if (this.status == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Project is already archived");
        }
        this.status = ProjectStatus.ARCHIVED;
    }

    public void activate() {
        if (this.status == ProjectStatus.DELETED) {
            throw new IllegalStateException("Deleted project cannot be activated");
        }
        if (this.status != ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Only archived project can be activated");
        }
        this.status = ProjectStatus.ACTIVE;
    }

    public void softDelete() {
        if (this.status == ProjectStatus.DELETED) {
            throw new IllegalStateException("Project is already deleted");
        }
        this.status = ProjectStatus.DELETED;
    }

    public boolean isOwner(Long userId) {
        return this.ownerId.equals(userId);
    }

    private void assertMutable() {
        if (this.status == ProjectStatus.ARCHIVED || this.status == ProjectStatus.DELETED) {
            throw new IllegalStateException("Cannot update archived or deleted project");
        }
    }
}
