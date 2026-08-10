package com.bracketops.infrastructure.persistence.adapter;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.valueobject.MatchRound;
import com.bracketops.domain.model.valueobject.MatchStatus;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.infrastructure.persistence.entity.MatchJpaEntity;
import com.bracketops.infrastructure.persistence.repository.SpringDataMatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MatchPersistenceAdapter implements MatchRepositoryPort {

    private final SpringDataMatchRepository repository;

    public MatchPersistenceAdapter(SpringDataMatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Match save(Match match) {
        MatchJpaEntity entity = mapToEntity(match);
        MatchJpaEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Match> saveAll(List<Match> matches) {
        List<MatchJpaEntity> entities = matches.stream().map(this::mapToEntity).collect(Collectors.toList());
        return repository.saveAll(entities).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Match> findById(String id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Match> findByTournamentId(String tournamentId) {
        return repository.findByTournamentId(tournamentId).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<Match> findByTournamentIdAndRound(String tournamentId, MatchRound round) {
        return repository.findByTournamentIdAndRound(tournamentId, round.name()).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    private MatchJpaEntity mapToEntity(Match domain) {
        MatchJpaEntity entity = new MatchJpaEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setRound(domain.getRound().name());
        entity.setMatchOrder(domain.getMatchOrder());
        entity.setTeamAId(domain.getTeamAId());
        entity.setTeamAName(domain.getTeamAName());
        entity.setTeamBId(domain.getTeamBId());
        entity.setTeamBName(domain.getTeamBName());
        entity.setTeamAScore(domain.getTeamAScore());
        entity.setTeamBScore(domain.getTeamBScore());
        entity.setWinnerTeamId(domain.getWinnerTeamId());
        entity.setWinnerTeamName(domain.getWinnerTeamName());
        entity.setStatus(domain.getStatus().name());
        entity.setNextMatchId(domain.getNextMatchId());
        entity.setLoserNextMatchId(domain.getLoserNextMatchId());
        entity.setVersion(domain.getVersion());
        entity.setScheduledTime(domain.getScheduledTime());
        return entity;
    }

    private Match mapToDomain(MatchJpaEntity entity) {
        return new Match(
                entity.getId(),
                entity.getTournamentId(),
                MatchRound.valueOf(entity.getRound()),
                entity.getMatchOrder(),
                entity.getTeamAId(),
                entity.getTeamAName(),
                entity.getTeamBId(),
                entity.getTeamBName(),
                entity.getTeamAScore(),
                entity.getTeamBScore(),
                entity.getWinnerTeamId(),
                entity.getWinnerTeamName(),
                MatchStatus.valueOf(entity.getStatus()),
                entity.getNextMatchId(),
                entity.getLoserNextMatchId(),
                entity.getVersion(),
                entity.getScheduledTime()
        );
    }
}
