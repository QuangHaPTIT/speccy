package com.speccy.speccy.domain.workspace.model;

import com.speccy.speccy.application.utils.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "test_data_profiles")
public class TestDataProfile {

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

    @NotBlank
    @Column(name = "variables_json", nullable = false, columnDefinition = "LONGTEXT")
    private String variablesJson;

    @NotNull
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public TestDataProfile(Long projectId, String name, String variablesJson, Boolean isDefault) {
        this.projectId = projectId;
        this.name = name;
        this.variablesJson = variablesJson;
        this.isDefault = isDefault;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Util.getNowUTC();
        }
        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }

    public void updateProfile(String name, String variablesJson) {
        this.name = name;
        this.variablesJson = variablesJson;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
