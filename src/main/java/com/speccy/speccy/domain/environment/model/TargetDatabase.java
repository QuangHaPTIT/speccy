package com.speccy.speccy.domain.environment.model;

import com.speccy.speccy.application.converter.TargetDatabaseStatusConverter;
import com.speccy.speccy.application.utils.Util;
import com.speccy.speccy.domain.shared.AuditableAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "target_databases")
public class TargetDatabase extends AuditableAggregateRoot<TargetDatabase> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "db_type", nullable = false, length = 50)
    private ProjectDbType dbType;

    @NotBlank
    @Size(max = 255)
    @Column(name = "host", nullable = false, length = 255)
    private String host;

    @NotNull
    @Min(1)
    @Max(65535)
    @Column(name = "port", nullable = false)
    private Integer port;

    @NotBlank
    @Size(max = 255)
    @Column(name = "database_name", nullable = false, length = 255)
    private String databaseName;

    @NotBlank
    @Size(max = 255)
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @NotBlank
    @Size(max = 500)
    @Column(name = "password_enc", nullable = false, length = 500)
    private String passwordEnc;

    @NotNull
    @Convert(converter = TargetDatabaseStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private TargetDatabaseStatus status = TargetDatabaseStatus.UNTESTED;

    @Column(name = "last_tested_at")
    private LocalDateTime lastTestedAt;

    public TargetDatabase(
            Long projectId,
            String name,
            ProjectDbType dbType,
            String host,
            Integer port,
            String databaseName,
            String username,
            String passwordEnc
    ) {
        this.projectId = projectId;
        this.name = name;
        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.passwordEnc = passwordEnc;
    }

    public void updateConfig(
            String name,
            ProjectDbType dbType,
            String host,
            Integer port,
            String databaseName,
            String username,
            String passwordEnc
    ) {
        this.name = name;
        this.dbType = dbType;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.passwordEnc = passwordEnc;
    }

    public void markActive() {
        this.status = TargetDatabaseStatus.ACTIVE;
        this.lastTestedAt = Util.getNowUTC();
    }

    public void markUnreachable() {
        this.status = TargetDatabaseStatus.UNREACHABLE;
    }

    public void markUntested() {
        this.status = TargetDatabaseStatus.UNTESTED;
    }
}
