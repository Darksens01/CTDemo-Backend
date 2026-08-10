package com.bracketops.domain.service;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.valueobject.MatchRound;
import com.bracketops.domain.model.valueobject.MatchStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BracketGeneratorEngine {

    public List<Match> generateBracket(Tournament tournament, List<Team> registeredTeams) {
        String format = tournament.getFormat() != null ? tournament.getFormat() : "SINGLE_ELIMINATION";
        switch (format) {
            case "DOUBLE_ELIMINATION":
                return generateDoubleEliminationBracket(tournament, registeredTeams);
            case "ROUND_ROBIN":
                return generateRoundRobinSchedule(tournament, registeredTeams);
            case "SWISS":
                return generateSwissSchedule(tournament, registeredTeams);
            case "FREE_FOR_ALL":
                return generateFreeForAllRounds(tournament, registeredTeams);
            default:
                return generateSingleEliminationBracket(tournament, registeredTeams);
        }
    }

    // 1. Single Elimination (Adaptive Rounds)
    public List<Match> generateSingleEliminationBracket(Tournament tournament, List<Team> registeredTeams) {
        List<Match> generatedMatches = new ArrayList<>();
        List<Team> shuffledTeams = new ArrayList<>(registeredTeams);
        Collections.shuffle(shuffledTeams);

        int teamCount = shuffledTeams.size();

        if (teamCount <= 2) {
            String teamAId = teamCount > 0 ? shuffledTeams.get(0).getId() : null;
            String teamAName = teamCount > 0 ? shuffledTeams.get(0).getTeamName() : "BYE";
            String teamBId = teamCount > 1 ? shuffledTeams.get(1).getId() : null;
            String teamBName = teamCount > 1 ? shuffledTeams.get(1).getTeamName() : "BYE";

            Match grandFinal = new Match(
                    UUID.randomUUID().toString(), tournament.getId(), MatchRound.GRAND_FINALS, 1,
                    teamAId, teamAName, teamBId, teamBName,
                    0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                    tournament.getStartDate()
            );
            generatedMatches.add(grandFinal);
            return generatedMatches;
        }

        if (teamCount <= 4) {
            String grandFinalId = UUID.randomUUID().toString();
            Match grandFinal = new Match(
                    grandFinalId, tournament.getId(), MatchRound.GRAND_FINALS, 1,
                    null, "TBD (Ganador Semi 1)", null, "TBD (Ganador Semi 2)",
                    0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                    tournament.getStartDate().plusDays(1)
            );

            String team1AId = shuffledTeams.size() > 0 ? shuffledTeams.get(0).getId() : null;
            String team1AName = shuffledTeams.size() > 0 ? shuffledTeams.get(0).getTeamName() : "BYE";
            String team1BId = shuffledTeams.size() > 1 ? shuffledTeams.get(1).getId() : null;
            String team1BName = shuffledTeams.size() > 1 ? shuffledTeams.get(1).getTeamName() : "BYE";

            Match semi1 = new Match(
                    UUID.randomUUID().toString(), tournament.getId(), MatchRound.SEMIFINALS, 1,
                    team1AId, team1AName, team1BId, team1BName,
                    0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, null,
                    tournament.getStartDate()
            );

            String team2AId = shuffledTeams.size() > 2 ? shuffledTeams.get(2).getId() : null;
            String team2AName = shuffledTeams.size() > 2 ? shuffledTeams.get(2).getTeamName() : "BYE";
            String team2BId = shuffledTeams.size() > 3 ? shuffledTeams.get(3).getId() : null;
            String team2BName = shuffledTeams.size() > 3 ? shuffledTeams.get(3).getTeamName() : "BYE";

            Match semi2 = new Match(
                    UUID.randomUUID().toString(), tournament.getId(), MatchRound.SEMIFINALS, 2,
                    team2AId, team2AName, team2BId, team2BName,
                    0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, null,
                    tournament.getStartDate()
            );

            generatedMatches.add(semi1);
            generatedMatches.add(semi2);
            generatedMatches.add(grandFinal);
            return generatedMatches;
        }

        // 5-8 Teams
        String grandFinalId = UUID.randomUUID().toString();
        Match grandFinal = new Match(
                grandFinalId, tournament.getId(), MatchRound.GRAND_FINALS, 1,
                null, "TBD (Ganador Semi 1)", null, "TBD (Ganador Semi 2)",
                0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                tournament.getStartDate().plusDays(2)
        );

        String semiFinal1Id = UUID.randomUUID().toString();
        Match semiFinal1 = new Match(
                semiFinal1Id, tournament.getId(), MatchRound.SEMIFINALS, 1,
                null, "TBD (Ganador QF 1)", null, "TBD (Ganador QF 2)",
                0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, null,
                tournament.getStartDate().plusDays(1)
        );

        String semiFinal2Id = UUID.randomUUID().toString();
        Match semiFinal2 = new Match(
                semiFinal2Id, tournament.getId(), MatchRound.SEMIFINALS, 2,
                null, "TBD (Ganador QF 3)", null, "TBD (Ganador QF 4)",
                0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, null,
                tournament.getStartDate().plusDays(1)
        );

        for (int i = 0; i < 4; i++) {
            String teamAId = (i * 2 < shuffledTeams.size()) ? shuffledTeams.get(i * 2).getId() : null;
            String teamAName = (i * 2 < shuffledTeams.size()) ? shuffledTeams.get(i * 2).getTeamName() : "BYE";

            String teamBId = (i * 2 + 1 < shuffledTeams.size()) ? shuffledTeams.get(i * 2 + 1).getId() : null;
            String teamBName = (i * 2 + 1 < shuffledTeams.size()) ? shuffledTeams.get(i * 2 + 1).getTeamName() : "BYE";

            String targetSemiId = (i < 2) ? semiFinal1Id : semiFinal2Id;

            Match qfMatch = new Match(
                    UUID.randomUUID().toString(), tournament.getId(), MatchRound.QUARTERFINALS, i + 1,
                    teamAId, teamAName, teamBId, teamBName,
                    0, 0, null, null, MatchStatus.SCHEDULED, targetSemiId, null,
                    tournament.getStartDate()
            );
            generatedMatches.add(qfMatch);
        }

        generatedMatches.add(semiFinal1);
        generatedMatches.add(semiFinal2);
        generatedMatches.add(grandFinal);

        return generatedMatches;
    }

    // 2. Double Elimination (Winners Bracket + Losers Bracket + Grand Finals)
    public List<Match> generateDoubleEliminationBracket(Tournament tournament, List<Team> registeredTeams) {
        List<Match> matches = new ArrayList<>();
        List<Team> teams = new ArrayList<>(registeredTeams);
        Collections.shuffle(teams);

        String grandFinalId = UUID.randomUUID().toString();
        Match grandFinal = new Match(
                grandFinalId, tournament.getId(), MatchRound.GRAND_FINALS, 1,
                null, "TBD (Ganador Winners)", null, "TBD (Ganador Repesca)",
                0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                tournament.getStartDate().plusDays(3)
        );

        // Losers Final
        String losersFinalId = UUID.randomUUID().toString();
        Match losersFinal = new Match(
                losersFinalId, tournament.getId(), MatchRound.LOSERS_FINALS, 1,
                null, "TBD (Perdedor Winners Final)", null, "TBD (Ganador Losers R1)",
                0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, null,
                tournament.getStartDate().plusDays(2)
        );

        // Losers Round 1
        String losersR1Id = UUID.randomUUID().toString();
        Match losersR1 = new Match(
                losersR1Id, tournament.getId(), MatchRound.LOSERS_ROUND_1, 1,
                null, "TBD (Perdedor Semi 1)", null, "TBD (Perdedor Semi 2)",
                0, 0, null, null, MatchStatus.SCHEDULED, losersFinalId, null, null,
                tournament.getStartDate().plusDays(1)
        );

        // Winners Final
        String winnersFinalId = UUID.randomUUID().toString();
        Match winnersFinal = new Match(
                winnersFinalId, tournament.getId(), MatchRound.WINNERS_FINALS, 1,
                null, "TBD (Semi 1 Winner)", null, "TBD (Semi 2 Winner)",
                0, 0, null, null, MatchStatus.SCHEDULED, grandFinalId, losersFinalId, null,
                tournament.getStartDate().plusDays(2)
        );

        // Winners Semifinals
        String t1A = teams.size() > 0 ? teams.get(0).getId() : null;
        String t1AName = teams.size() > 0 ? teams.get(0).getTeamName() : "BYE";
        String t1B = teams.size() > 1 ? teams.get(1).getId() : null;
        String t1BName = teams.size() > 1 ? teams.get(1).getTeamName() : "BYE";

        Match semi1 = new Match(
                UUID.randomUUID().toString(), tournament.getId(), MatchRound.SEMIFINALS, 1,
                t1A, t1AName, t1B, t1BName,
                0, 0, null, null, MatchStatus.SCHEDULED, winnersFinalId, losersR1Id, null,
                tournament.getStartDate()
        );

        String t2A = teams.size() > 2 ? teams.get(2).getId() : null;
        String t2AName = teams.size() > 2 ? teams.get(2).getTeamName() : "BYE";
        String t2B = teams.size() > 3 ? teams.get(3).getId() : null;
        String t2BName = teams.size() > 3 ? teams.get(3).getTeamName() : "BYE";

        Match semi2 = new Match(
                UUID.randomUUID().toString(), tournament.getId(), MatchRound.SEMIFINALS, 2,
                t2A, t2AName, t2B, t2BName,
                0, 0, null, null, MatchStatus.SCHEDULED, winnersFinalId, losersR1Id, null,
                tournament.getStartDate()
        );

        matches.add(semi1);
        matches.add(semi2);
        matches.add(losersR1);
        matches.add(winnersFinal);
        matches.add(losersFinal);
        matches.add(grandFinal);
        return matches;
    }

    // 3. Round Robin (Liga - Todos contra Todos)
    public List<Match> generateRoundRobinSchedule(Tournament tournament, List<Team> registeredTeams) {
        List<Match> matches = new ArrayList<>();
        List<Team> teams = new ArrayList<>(registeredTeams);
        int n = teams.size();
        int matchOrder = 1;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Team tA = teams.get(i);
                Team tB = teams.get(j);

                Match m = new Match(
                        UUID.randomUUID().toString(), tournament.getId(), MatchRound.ROUND_ROBIN, matchOrder++,
                        tA.getId(), tA.getTeamName(), tB.getId(), tB.getTeamName(),
                        0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                        tournament.getStartDate()
                );
                matches.add(m);
            }
        }
        return matches;
    }

    // 4. Swiss System (Sistema Suizo)
    public List<Match> generateSwissSchedule(Tournament tournament, List<Team> registeredTeams) {
        List<Match> matches = new ArrayList<>();
        List<Team> teams = new ArrayList<>(registeredTeams);
        Collections.shuffle(teams);
        int matchOrder = 1;

        for (int i = 0; i < teams.size(); i += 2) {
            Team tA = teams.get(i);
            Team tB = (i + 1 < teams.size()) ? teams.get(i + 1) : null;

            Match m = new Match(
                    UUID.randomUUID().toString(), tournament.getId(), MatchRound.SWISS_ROUND_1, matchOrder++,
                    tA.getId(), tA.getTeamName(), tB != null ? tB.getId() : null, tB != null ? tB.getTeamName() : "BYE",
                    0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                    tournament.getStartDate()
            );
            matches.add(m);
        }
        return matches;
    }

    // 5. Free-for-All (Batalla Real)
    public List<Match> generateFreeForAllRounds(Tournament tournament, List<Team> registeredTeams) {
        List<Match> matches = new ArrayList<>();
        for (int roundNum = 1; roundNum <= 3; roundNum++) {
            int matchOrder = 1;
            for (int i = 0; i < registeredTeams.size(); i += 2) {
                Team tA = registeredTeams.get(i);
                Team tB = (i + 1 < registeredTeams.size()) ? registeredTeams.get(i + 1) : null;

                Match m = new Match(
                        UUID.randomUUID().toString(), tournament.getId(), MatchRound.FFA_ROUND_1, matchOrder++,
                        tA.getId(), tA.getTeamName(), tB != null ? tB.getId() : null, tB != null ? tB.getTeamName() : "BYE",
                        0, 0, null, null, MatchStatus.SCHEDULED, null, null,
                        tournament.getStartDate()
                );
                matches.add(m);
            }
        }
        return matches;
    }
}
