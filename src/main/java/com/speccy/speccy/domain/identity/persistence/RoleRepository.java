package com.speccy.speccy.domain.identity.persistence;

import com.speccy.speccy.domain.identity.model.Role;

import java.util.Optional;

public interface RoleRepository {
    void save(Role role);

    Optional<Role> findByCode(String code);
}
