package com.bracketops.infrastructure.persistence.repository;

import com.bracketops.infrastructure.persistence.entity.MatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataMatchRepository extends JpaRepository<MatchJpaEntity, String> {
    List<MatchJpaEntity> findByTournamentId(String tournamentId);
    List<MatchJpaEntity> findByTournamentIdAndRound(String tournamentId, String round);
}
