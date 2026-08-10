package com.bracketops.application.command.handler;

import com.bracketops.application.dto.MatchResponseDto;
import com.bracketops.application.dto.UpdateMatchScoreCommand;
import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.exception.ResourceNotFoundException;
import com.bracketops.domain.model.valueobject.MatchRound;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.NotificationPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.domain.service.MatchAdvancementEngine;

import java.util.List;

public class UpdateMatchScoreCommandHandler {

    private final MatchRepositoryPort matchRepositoryPort;
    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final NotificationPort notificationPort;
    private final MatchAdvancementEngine advancementEngine;

    public UpdateMatchScoreCommandHandler(MatchRepositoryPort matchRepositoryPort,
                                          TournamentRepositoryPort tournamentRepositoryPort,
                                          NotificationPort notificationPort,
                                          MatchAdvancementEngine advancementEngine) {
        this.matchRepositoryPort = matchRepositoryPort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.notificationPort = notificationPort;
        this.advancementEngine = advancementEngine;
    }

    public MatchResponseDto handle(UpdateMatchScoreCommand command) {
        Match match = matchRepositoryPort.findById(command.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match", command.matchId()));

        Tournament tournament = tournamentRepositoryPort.findById(match.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", match.getTournamentId()));

        // Update score & calculate winner
        match.updateScore(command.teamAScore(), command.teamBScore());
        Match savedMatch = matchRepositoryPort.save(match);

        // Send Push Notification
        String loserName = savedMatch.getWinnerTeamId().equals(savedMatch.getTeamAId()) ? savedMatch.getTeamBName() : savedMatch.getTeamAName();
        int winnerScore = Math.max(command.teamAScore(), command.teamBScore());
        int loserScore = Math.min(command.teamAScore(), command.teamBScore());

        notificationPort.sendMatchUpdateNotification(
                tournament.getName(),
                savedMatch.getRound().getDisplayName(),
                savedMatch.getWinnerTeamName(),
                loserName,
                winnerScore,
                loserScore
        );

        // Advance winner to next round match slot if exists
        if (savedMatch.getNextMatchId() != null) {
            matchRepositoryPort.findById(savedMatch.getNextMatchId()).ifPresent(nextMatch -> {
                advancementEngine.advanceWinnerToNextMatch(savedMatch, nextMatch);
                matchRepositoryPort.save(nextMatch);
            });
        }

        // Advance loser to loser next round match slot if exists (Double Elimination)
        if (savedMatch.getLoserNextMatchId() != null) {
            matchRepositoryPort.findById(savedMatch.getLoserNextMatchId()).ifPresent(loserMatch -> {
                advancementEngine.advanceLoserToNextMatch(savedMatch, loserMatch);
                matchRepositoryPort.save(loserMatch);
            });
        } else {
            // Fallback for Double Elimination if loserNextMatchId was null
            List<Match> tMatches = matchRepositoryPort.findByTournamentId(tournament.getId());
            if (savedMatch.getRound() == MatchRound.SEMIFINALS) {
                for (Match m : tMatches) {
                    if (m.getRound() == MatchRound.LOSERS_ROUND_1) {
                        advancementEngine.advanceLoserToNextMatch(savedMatch, m);
                        matchRepositoryPort.save(m);
                        break;
                    }
                }
            } else if (savedMatch.getRound() == MatchRound.WINNERS_FINALS) {
                for (Match m : tMatches) {
                    if (m.getRound() == MatchRound.LOSERS_FINALS) {
                        advancementEngine.advanceLoserToNextMatch(savedMatch, m);
                        matchRepositoryPort.save(m);
                        break;
                    }
                }
            }
        }

        // If Grand Finals completed or all matches completed (Round Robin / Swiss / FFA), crown champion!
        if (savedMatch.getRound() == MatchRound.GRAND_FINALS) {
            tournament.crownChampion(savedMatch.getWinnerTeamId(), savedMatch.getWinnerTeamName());
            tournamentRepositoryPort.save(tournament);
            notificationPort.sendChampionCrownedNotification(tournament.getName(), savedMatch.getWinnerTeamName());
        } else {
            List<Match> allMatches = matchRepositoryPort.findByTournamentId(tournament.getId());
            boolean allMatchesCompleted = !allMatches.isEmpty() && allMatches.stream()
                    .allMatch(m -> m.getStatus() == com.bracketops.domain.model.valueobject.MatchStatus.COMPLETED);

            if (allMatchesCompleted) {
                // Calculate standings table for non-elimination formats
                java.util.Map<String, int[]> stats = new java.util.HashMap<>(); // [points, roundDiff, scoreFor]
                java.util.Map<String, String> names = new java.util.HashMap<>();

                for (Match m : allMatches) {
                    if (m.getTeamAId() != null) {
                        names.put(m.getTeamAId(), m.getTeamAName());
                        stats.putIfAbsent(m.getTeamAId(), new int[3]);
                    }
                    if (m.getTeamBId() != null) {
                        names.put(m.getTeamBId(), m.getTeamBName());
                        stats.putIfAbsent(m.getTeamBId(), new int[3]);
                    }

                    if (m.getWinnerTeamId() != null) {
                        String winner = m.getWinnerTeamId();
                        String loser = winner.equals(m.getTeamAId()) ? m.getTeamBId() : m.getTeamAId();

                        if (stats.containsKey(winner)) {
                            stats.get(winner)[0] += 3; // 3 points for win
                            int diff = Math.abs(m.getTeamAScore() - m.getTeamBScore());
                            stats.get(winner)[1] += diff;
                            stats.get(winner)[2] += Math.max(m.getTeamAScore(), m.getTeamBScore());
                        }
                        if (loser != null && stats.containsKey(loser)) {
                            int diff = Math.abs(m.getTeamAScore() - m.getTeamBScore());
                            stats.get(loser)[1] -= diff;
                            stats.get(loser)[2] += Math.min(m.getTeamAScore(), m.getTeamBScore());
                        }
                    } else if (m.getTeamAId() != null && m.getTeamBId() != null) {
                        // Draw: 1 point each
                        if (stats.containsKey(m.getTeamAId())) stats.get(m.getTeamAId())[0] += 1;
                        if (stats.containsKey(m.getTeamBId())) stats.get(m.getTeamBId())[0] += 1;
                    }
                }

                String champId = stats.entrySet().stream()
                        .max((e1, e2) -> {
                            int[] s1 = e1.getValue();
                            int[] s2 = e2.getValue();
                            if (s1[0] != s2[0]) return Integer.compare(s1[0], s2[0]);
                            if (s1[1] != s2[1]) return Integer.compare(s1[1], s2[1]);
                            return Integer.compare(s1[2], s2[2]);
                        })
                        .map(java.util.Map.Entry::getKey)
                        .orElse(null);

                if (champId != null && names.containsKey(champId)) {
                    tournament.crownChampion(champId, names.get(champId));
                    tournamentRepositoryPort.save(tournament);
                    notificationPort.sendChampionCrownedNotification(tournament.getName(), names.get(champId));
                }
            }
        }

        return mapToDto(savedMatch);
    }

    private MatchResponseDto mapToDto(Match match) {
        return new MatchResponseDto(
                match.getId(),
                match.getTournamentId(),
                match.getRound().name(),
                match.getRound().getDisplayName(),
                match.getMatchOrder(),
                match.getTeamAId(),
                match.getTeamAName(),
                match.getTeamBId(),
                match.getTeamBName(),
                match.getTeamAScore(),
                match.getTeamBScore(),
                match.getWinnerTeamId(),
                match.getWinnerTeamName(),
                match.getStatus().name(),
                match.getNextMatchId(),
                match.getLoserNextMatchId(),
                match.getVersion(),
                match.getScheduledTime()
        );
    }
}
