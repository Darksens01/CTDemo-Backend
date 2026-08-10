package com.bracketops.infrastructure.persistence.repository;

import com.bracketops.infrastructure.persistence.entity.TeamJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataTeamRepository extends JpaRepository<TeamJpaEntity, String> {
    Optional<TeamJpaEntity> findByCaptainUsername(String captainUsername);
    boolean existsByTeamName(String teamName);
}
