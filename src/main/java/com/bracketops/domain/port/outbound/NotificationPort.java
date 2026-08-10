package com.bracketops.domain.port.outbound;

public interface NotificationPort {
    void sendMatchUpdateNotification(String tournamentName, String roundName, String winnerTeamName, String loserTeamName, int winnerScore, int loserScore);
    void sendChampionCrownedNotification(String tournamentName, String championTeamName);
    void sendTournamentCancelledNotification(String tournamentName, String reason);
}
