package com.bracketops.domain.port.outbound;

import com.bracketops.domain.model.aggregate.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepositoryPort {
    Team save(Team team);
    Optional<Team> findById(String id);
    Optional<Team> findByCaptainUsername(String captainUsername);
    List<Team> findAll();
    List<Team> findByIds(List<String> ids);
    boolean existsByTeamName(String teamName);
}
