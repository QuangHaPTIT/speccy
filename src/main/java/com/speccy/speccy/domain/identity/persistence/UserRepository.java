package com.speccy.speccy.domain.identity.persistence;

import com.speccy.speccy.domain.identity.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
