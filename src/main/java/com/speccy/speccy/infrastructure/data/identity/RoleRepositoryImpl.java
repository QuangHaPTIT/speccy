package com.speccy.speccy.infrastructure.data.identity;

import com.speccy.speccy.domain.identity.model.Role;
import com.speccy.speccy.domain.identity.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public void save(Role role) {
        jpaRoleRepository.save(role);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return jpaRoleRepository.findByCode(code);
    }
}
