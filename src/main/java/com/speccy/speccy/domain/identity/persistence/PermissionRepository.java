package com.speccy.speccy.domain.user.persistence;

import com.speccy.speccy.domain.user.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {
    void save(Permission permission);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByName(String name);

    List<Permission> findAllByRole(String role);
}
