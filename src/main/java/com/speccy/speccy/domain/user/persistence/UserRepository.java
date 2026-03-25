package com.speccy.speccy.domain.user.persistence;

import com.speccy.speccy.domain.user.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
