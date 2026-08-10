package com.bracketops.infrastructure.persistence.adapter;

import com.bracketops.domain.model.entity.UserDomain;
import com.bracketops.domain.model.valueobject.Role;
import com.bracketops.domain.port.outbound.UserRepositoryPort;
import com.bracketops.infrastructure.persistence.entity.UserJpaEntity;
import com.bracketops.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDomain save(UserDomain user) {
        UserJpaEntity entity = new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive()
        );
        UserJpaEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<UserDomain> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<UserDomain> findByUsername(String username) {
        return repository.findByUsername(username).map(this::mapToDomain);
    }

    @Override
    public List<UserDomain> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    private UserDomain mapToDomain(UserJpaEntity entity) {
        return new UserDomain(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getFullName(),
                entity.getEmail(),
                Role.valueOf(entity.getRole()),
                entity.isActive()
        );
    }
}
