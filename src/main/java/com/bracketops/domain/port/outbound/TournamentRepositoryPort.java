package com.bracketops.domain.port.outbound;

import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.valueobject.TournamentStatus;

import java.util.List;
import java.util.Optional;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(String id);
    List<Tournament> findAll();
    List<Tournament> findByStatus(TournamentStatus status);
    void deleteById(String id);
}
