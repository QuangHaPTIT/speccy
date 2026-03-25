package com.speccy.speccy.domain.user.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "project_members",
        uniqueConstraints = @UniqueConstraint(name = "uq_member", columnNames = {"project_id", "user_id"})
)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ProjectRole role;

    @Column(name = "invited_by")
    private Long invitedBy;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public ProjectMember(Long projectId, Long userId, ProjectRole role, Long invitedBy) {
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
        this.invitedBy = invitedBy;
        this.joinedAt = Instant.now();
    }

    @PrePersist
    protected void initJoinedAt() {
        if (this.joinedAt == null) {
            this.joinedAt = Instant.now();
        }
    }

    public void changeRole(ProjectRole role) {
        this.role = role;
    }
}
