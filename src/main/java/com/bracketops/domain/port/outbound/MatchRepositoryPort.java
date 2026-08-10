package com.bracketops.domain.port.outbound;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.valueobject.MatchRound;

import java.util.List;
import java.util.Optional;

public interface MatchRepositoryPort {
    Match save(Match match);
    List<Match> saveAll(List<Match> matches);
    Optional<Match> findById(String id);
    List<Match> findByTournamentId(String tournamentId);
    List<Match> findByTournamentIdAndRound(String tournamentId, MatchRound round);
}
