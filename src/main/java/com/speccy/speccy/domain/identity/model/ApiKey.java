package com.speccy.speccy.domain.identity.model;

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

import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "api_keys")
public class ApiKey extends AuditableAggregateRoot<ApiKey> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "key_hash", nullable = false, unique = true, length = 255)
    private String keyHash;

    @NotBlank
    @Size(max = 20)
    @Column(name = "key_prefix", nullable = false, length = 20)
    private String keyPrefix;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.DEVELOPER;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public ApiKey(Long userId, Long projectId, String name, String keyHash, String keyPrefix, Role role, LocalDateTime expiresAt) {
        this.userId = userId;
        this.projectId = projectId;
        this.name = name;
        this.keyHash = keyHash;
        this.keyPrefix = keyPrefix;
        this.role = role == null ? Role.DEVELOPER : role;
        this.expiresAt = expiresAt;
    }

    public void markUsed(LocalDateTime usedAt) {
        this.lastUsedAt = usedAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiresAt != null && this.expiresAt.isBefore(now);
    }
}
