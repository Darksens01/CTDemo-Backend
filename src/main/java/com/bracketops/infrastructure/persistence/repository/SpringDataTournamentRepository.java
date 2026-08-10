package com.bracketops.infrastructure.persistence.repository;

import com.bracketops.infrastructure.persistence.entity.TournamentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataTournamentRepository extends JpaRepository<TournamentJpaEntity, String> {
    List<TournamentJpaEntity> findByStatus(String status);
}
