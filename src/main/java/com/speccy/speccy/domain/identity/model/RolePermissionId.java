package com.speccy.speccy.domain.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RolePermissionId implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ProjectRole role;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;
}
