package com.speccy.speccy.domain.identity.persistence;


import com.speccy.speccy.domain.identity.model.Role;
import com.speccy.speccy.domain.identity.model.RolePermission;

import java.util.List;

public interface RolePermissionRepository {
    void save(RolePermission rolePermission);

    void deleteByRoleAndPermissionId(Role role, Long permissionId);

    List<RolePermission> findByRole(Role role);
}
