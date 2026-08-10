package com.bracketops.infrastructure.persistence.adapter;

import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.infrastructure.persistence.entity.TournamentJpaEntity;
import com.bracketops.infrastructure.persistence.repository.SpringDataTournamentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TournamentPersistenceAdapter implements TournamentRepositoryPort {

    private final SpringDataTournamentRepository repository;

    public TournamentPersistenceAdapter(SpringDataTournamentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tournament save(Tournament tournament) {
        TournamentJpaEntity entity = mapToEntity(tournament);
        TournamentJpaEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Tournament> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tournament> findByStatus(TournamentStatus status) {
        return repository.findByStatus(status.name()).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private TournamentJpaEntity mapToEntity(Tournament domain) {
        TournamentJpaEntity entity = new TournamentJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setGameName(domain.getGameName());
        entity.setFormat(domain.getFormat());
        entity.setMaxTeams(domain.getMaxTeams());
        entity.setPlayersPerTeam(domain.getPlayersPerTeam());
        entity.setStatus(domain.getStatus().name());
        entity.setPrizePool(domain.getPrizePool());
        entity.setBannerUrl(domain.getBannerUrl());
        entity.setCancellationReason(domain.getCancellationReason());
        entity.setRegisteredTeamIds(domain.getRegisteredTeamIds());
        entity.setChampionTeamId(domain.getChampionTeamId());
        entity.setChampionTeamName(domain.getChampionTeamName());
        entity.setStartDate(domain.getStartDate());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private Tournament mapToDomain(TournamentJpaEntity entity) {
        return new Tournament(
                entity.getId(),
                entity.getName(),
                entity.getGameName(),
                entity.getFormat(),
                entity.getMaxTeams(),
                entity.getPlayersPerTeam(),
                TournamentStatus.valueOf(entity.getStatus()),
                entity.getPrizePool(),
                entity.getBannerUrl(),
                entity.getCancellationReason(),
                entity.getRegisteredTeamIds(),
                entity.getChampionTeamId(),
                entity.getChampionTeamName(),
                entity.getStartDate(),
                entity.getCreatedAt()
        );
    }
}
