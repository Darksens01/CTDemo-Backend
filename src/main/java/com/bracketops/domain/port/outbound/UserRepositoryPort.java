package com.bracketops.domain.port.outbound;

import com.bracketops.domain.model.entity.UserDomain;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    UserDomain save(UserDomain user);
    Optional<UserDomain> findById(String id);
    Optional<UserDomain> findByUsername(String username);
    List<UserDomain> findAll();
    boolean existsByUsername(String username);
}
