package com.speccy.speccy.infrastructure.data.identity;

import com.speccy.speccy.domain.identity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
}
