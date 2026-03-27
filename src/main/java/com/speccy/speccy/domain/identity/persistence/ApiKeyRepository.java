package com.speccy.speccy.domain.identity.persistence;


import com.speccy.speccy.domain.identity.model.ApiKey;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository {
    void save(ApiKey apiKey);

    Optional<ApiKey> findById(Long id);

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByProjectId(Long projectId);

    List<ApiKey> findByUserId(Long userId);

    void deleteById(Long id);
}
