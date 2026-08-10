package com.bracketops.domain.service;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.valueobject.MatchRound;

public class MatchAdvancementEngine {

    public void advanceWinnerToNextMatch(Match completedMatch, Match targetNextMatch) {
        if (targetNextMatch == null || completedMatch.getWinnerTeamId() == null) {
            return;
        }

        String winnerId = completedMatch.getWinnerTeamId();
        String winnerName = completedMatch.getWinnerTeamName();

        // Winner of LOSERS_ROUND_1 or LOSERS_FINALS always goes to Team B slot of the target match!
        if (completedMatch.getRound() == MatchRound.LOSERS_ROUND_1 || completedMatch.getRound() == MatchRound.LOSERS_FINALS) {
            targetNextMatch.setTeamB(winnerId, winnerName);
            return;
        }

        // Winner of WINNERS_FINALS always goes to Team A slot of GRAND_FINALS!
        if (completedMatch.getRound() == MatchRound.WINNERS_FINALS) {
            targetNextMatch.setTeamA(winnerId, winnerName);
            return;
        }

        // General placement based on matchOrder
        if (completedMatch.getMatchOrder() == 1) {
            targetNextMatch.setTeamA(winnerId, winnerName);
        } else if (completedMatch.getMatchOrder() == 2) {
            targetNextMatch.setTeamB(winnerId, winnerName);
        } else if (targetNextMatch.getTeamAId() == null || targetNextMatch.getTeamAName().contains("TBD")) {
            targetNextMatch.setTeamA(winnerId, winnerName);
        } else {
            targetNextMatch.setTeamB(winnerId, winnerName);
        }
    }

    public void advanceLoserToNextMatch(Match completedMatch, Match targetLoserMatch) {
        if (targetLoserMatch == null || completedMatch.getWinnerTeamId() == null) {
            return;
        }

        boolean isWinnerA = completedMatch.getWinnerTeamId().equals(completedMatch.getTeamAId());
        String loserId = isWinnerA ? completedMatch.getTeamBId() : completedMatch.getTeamAId();
        String loserName = isWinnerA ? completedMatch.getTeamBName() : completedMatch.getTeamAName();

        // Loser of WINNERS_FINALS always goes to Team A slot of LOSERS_FINALS!
        if (completedMatch.getRound() == MatchRound.WINNERS_FINALS) {
            targetLoserMatch.setTeamA(loserId, loserName);
            return;
        }

        // General placement
        if (completedMatch.getMatchOrder() == 1) {
            targetLoserMatch.setTeamA(loserId, loserName);
        } else if (completedMatch.getMatchOrder() == 2) {
            targetLoserMatch.setTeamB(loserId, loserName);
        } else if (targetLoserMatch.getTeamAId() == null || targetLoserMatch.getTeamAName().contains("TBD")) {
            targetLoserMatch.setTeamA(loserId, loserName);
        } else {
            targetLoserMatch.setTeamB(loserId, loserName);
        }
    }
}
