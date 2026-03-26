package com.speccy.speccy.domain.spec.model;

import jakarta.persistence.Column;
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
@Table(name = "api_modules")
public class ApiModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "spec_id", nullable = false)
    private Long specId;

    @NotNull
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 255)
    @Column(name = "base_path", length = 255)
    private String basePath;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    public ApiModule(Long specId, Long projectId, String name, String basePath, String description, Integer orderIndex) {
        this.specId = specId;
        this.projectId = projectId;
        this.name = name;
        this.basePath = basePath;
        this.description = description;
        this.orderIndex = orderIndex == null ? 0 : orderIndex;
    }

    public void updateInfo(String name, String basePath, String description, Integer orderIndex) {
        this.name = name;
        this.basePath = basePath;
        this.description = description;
        this.orderIndex = orderIndex == null ? 0 : orderIndex;
    }
}
