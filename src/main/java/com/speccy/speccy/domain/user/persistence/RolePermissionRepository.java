package com.speccy.speccy.domain.user.persistence;

import com.speccy.speccy.domain.user.model.ProjectRole;
import com.speccy.speccy.domain.user.model.RolePermission;

import java.util.List;

public interface RolePermissionRepository {
    void save(RolePermission rolePermission);

    void deleteByRoleAndPermissionId(ProjectRole role, Long permissionId);

    List<RolePermission> findByRole(ProjectRole role);
}
