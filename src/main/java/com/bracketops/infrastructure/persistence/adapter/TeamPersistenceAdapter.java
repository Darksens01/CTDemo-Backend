package com.bracketops.infrastructure.persistence.adapter;

import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.entity.Player;
import com.bracketops.domain.model.valueobject.TeamStatus;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.infrastructure.persistence.entity.PlayerJpaEntity;
import com.bracketops.infrastructure.persistence.entity.TeamJpaEntity;
import com.bracketops.infrastructure.persistence.repository.SpringDataTeamRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TeamPersistenceAdapter implements TeamRepositoryPort {

    private final SpringDataTeamRepository repository;

    public TeamPersistenceAdapter(SpringDataTeamRepository repository) {
        this.repository = repository;
    }

    @Override
    public Team save(Team team) {
        TeamJpaEntity entity = mapToEntity(team);
        TeamJpaEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Team> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Team> findByCaptainUsername(String captainUsername) {
        return repository.findByCaptainUsername(captainUsername).map(this::mapToDomain);
    }

    @Override
    public List<Team> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<Team> findByIds(List<String> ids) {
        return repository.findAllById(ids).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByTeamName(String teamName) {
        return repository.existsByTeamName(teamName);
    }

    private TeamJpaEntity mapToEntity(Team domain) {
        TeamJpaEntity entity = new TeamJpaEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setTeamName(domain.getTeamName());
        entity.setLogoUrl(domain.getLogoUrl());
        entity.setCaptainUsername(domain.getCaptainUsername());
        entity.setStatus(domain.getStatus().name());
        entity.setCreatedAt(domain.getCreatedAt());

        List<PlayerJpaEntity> playerEntities = domain.getPlayers().stream()
                .map(p -> new PlayerJpaEntity(p.getId(), p.getGamerTag(), p.getRealName(), p.getInGameRole()))
                .collect(Collectors.toList());

        entity.setPlayers(playerEntities);
        return entity;
    }

    private Team mapToDomain(TeamJpaEntity entity) {
        List<Player> players = entity.getPlayers().stream()
                .map(p -> new Player(p.getId(), p.getGamerTag(), p.getRealName(), p.getInGameRole()))
                .collect(Collectors.toList());

        return new Team(
                entity.getId(),
                entity.getTournamentId(),
                entity.getTeamName(),
                entity.getLogoUrl(),
                entity.getCaptainUsername(),
                TeamStatus.valueOf(entity.getStatus()),
                players,
                entity.getCreatedAt()
        );
    }
}
